/*
 * $Id$
 *
 * Copyright (c) 2005 The Codehaus - http://groovy.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */


package org.codehaus.groovy.intellij.compiler;

import java.io.IOException;

import org.intellij.openapi.testing.MockApplicationManager;

import com.intellij.compiler.progress.CompilerProgressIndicator;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.MockVirtualFile;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.ExceptionMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.control.messages.WarningMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.ast.ASTNode;

import org.jmock.Mock;
import org.jmock.core.Constraint;

import org.codehaus.groovy.intellij.GroovyController;
import org.codehaus.groovy.intellij.GroovySupportLoader;
import org.codehaus.groovy.intellij.GroovyjTestCase;
import org.codehaus.groovy.intellij.Mocks;
import org.codehaus.groovy.intellij.TestUtil;

import groovy.lang.MissingClassException;

public class GroovyCompilerTest extends GroovyjTestCase {

    private final Mock mockGroovyController = Mocks.createGroovyControllerMock(this);
    private final Mock mockFileTypeManager = mock(FileTypeManager.class);

    private final GroovyCompiler groovyCompiler = new GroovyCompiler((GroovyController) mockGroovyController.proxy());

    protected void setUp() {
        MockApplicationManager.getMockApplication().registerComponent(FileTypeManager.class, mockFileTypeManager.proxy());
    }

    protected void tearDown() {
        MockApplicationManager.getMockApplication().removeComponent(FileTypeManager.class);
    }

    public void testHasADescription() {
        assertEquals("description", "Groovy Compiler", groovyCompiler.getDescription());
    }

    public void testIsAlwaysReadyToParticipateInTheCompilationOfEitherProjectOrModule() {
        assertEquals("ready for compilation?", true, groovyCompiler.validateConfiguration(null));
    }

    public void testDeterminesThatAGivenFileIsCompilableWhenItsCorrespondingFileTypeIsTheGroovyOne() {
        VirtualFile file = new MockVirtualFile();
        mockFileTypeManager.expects(once()).method("getFileTypeByFile").with(same(file)).will(returnValue(GroovySupportLoader.GROOVY));
        assertEquals("is compilable?", true, groovyCompiler.isCompilableFile(file, null));
    }

    public void testDeterminesThatAGivenFileIsNotCompilableWhenItsCorrespondingFileTypeIsNotTheGroovyOne() {
        VirtualFile file = new MockVirtualFile();
        mockFileTypeManager.expects(once()).method("getFileTypeByFile").with(same(file)).will(returnValue(StdFileTypes.JAVA));
        assertEquals("is compilable?", false, groovyCompiler.isCompilableFile(file, null));
    }

    public void testCompilesNothingWhenThereAreNoFilesToCompile() {
        TranslatingCompiler.ExitStatus exitStatus = groovyCompiler.compile((CompileContext) mock(CompileContext.class).proxy(), VirtualFile.EMPTY_ARRAY);
        assertEquals("number of files to recompile", 0, exitStatus.getFilesToRecompile().length);
        assertEquals("number of files compiled", 0, exitStatus.getSuccessfullyCompiled().length);
    }

    public void testAbortsCompilationWhenCancelledInTheProgressBar() throws IOException {
        Mock mockCompileContext = mock(CompileContext.class);
        Mock mockProgressIndicator = mock(ProgressIndicator.class);
        mockCompileContext.stubs().method("getProgressIndicator").will(returnValue(mockProgressIndicator.proxy()));
        mockProgressIndicator.expects(once()).method("isCanceled").will(returnValue(true));

        VirtualFile[] filesToCompile = new VirtualFile[] { createVirtualFile("/home/foo/acme/src/bar.groovy") };

        TranslatingCompiler.ExitStatus exitStatus = groovyCompiler.compile((CompileContext) mockCompileContext.proxy(), filesToCompile);
        assertEquals("number of files to recompile", 0, exitStatus.getFilesToRecompile().length);
        assertEquals("number of files compiled", 0, exitStatus.getSuccessfullyCompiled().length);
    }

    public void testAddsSyntaxExceptionsWithPreciseLocationToCompileContext() throws IOException, CompilationFailedException {
        SyntaxException syntaxException = new SyntaxException("a syntax exception", TestUtil.nextAbsRandomInt(), TestUtil.nextAbsRandomInt());

        MockCompilationUnit compilationUnit = new MockCompilationUnit();
        ErrorCollector errorCollector = compilationUnit.getErrorCollector();
        errorCollector.addError(new SyntaxErrorMessage(syntaxException, null));
        compilationUnit.exception = new MultipleCompilationErrorsException(errorCollector);

        VirtualFile fileToCompile = createVirtualFile("/home/foo/acme/src/bar.groovy");

        Mock mockCompileContext = createMockedCompileContextWithStubbedProgressIndicator();
        mockCompileContext.expects(once()).method("addMessage")
                .with(new Constraint[] { same(CompilerMessageCategory.ERROR), eq(syntaxException.getMessage()),
                                         eq(fileToCompile.getUrl()), eq(syntaxException.getLine()), eq(syntaxException.getStartColumn()) });

        TranslatingCompiler.ExitStatus exitStatus = compile(fileToCompile, mockCompileContext, compilationUnit);
        assertEquals("number of files to recompile", 1, exitStatus.getFilesToRecompile().length);
        assertSame("file to recompile", fileToCompile, exitStatus.getFilesToRecompile()[0]);

        assertEquals("number of files compiled", 0, exitStatus.getSuccessfullyCompiled().length);

        assertEquals("number of errors", 1, errorCollector.getErrorCount());
        assertSame("exception", syntaxException, errorCollector.getException(0));

        assertEquals("number of warnings", 0, errorCollector.getWarningCount());
    }

    public void testAddsGroovyRuntimeExceptionsWithPreciseLocationToCompileContext() throws IOException, CompilationFailedException {
        ASTNode astNode = new ASTNode();
        astNode.setLineNumber(TestUtil.nextAbsRandomInt());
        astNode.setColumnNumber(TestUtil.nextAbsRandomInt());

        MissingClassException missingClassException = new MissingClassException("MadeUpType", astNode, "in blah blah");

        MockCompilationUnit compilationUnit = new MockCompilationUnit();
        ErrorCollector errorCollector = compilationUnit.getErrorCollector();
        errorCollector.addError(new ExceptionMessage(missingClassException, false, null));
        compilationUnit.exception = new MultipleCompilationErrorsException(errorCollector);

        VirtualFile fileToCompile = createVirtualFile("/home/foo/acme/src/bar.groovy");

        Mock mockCompileContext = createMockedCompileContextWithStubbedProgressIndicator();
        mockCompileContext.expects(once()).method("addMessage")
                .with(new Constraint[] { same(CompilerMessageCategory.ERROR),
                                         eq(missingClassException.getMessageWithoutLocationText()), eq(fileToCompile.getUrl()),
                                         eq(astNode.getLineNumber()), eq(astNode.getColumnNumber()) });

        TranslatingCompiler.ExitStatus exitStatus = compile(fileToCompile, mockCompileContext, compilationUnit);
        assertEquals("number of files to recompile", 1, exitStatus.getFilesToRecompile().length);
        assertSame("file to recompile", fileToCompile, exitStatus.getFilesToRecompile()[0]);

        assertEquals("number of files compiled", 0, exitStatus.getSuccessfullyCompiled().length);

        assertEquals("number of errors", 1, errorCollector.getErrorCount());
        assertSame("exception", missingClassException, errorCollector.getException(0));

        assertEquals("number of warnings", 0, errorCollector.getWarningCount());
    }

    public void testAddsExceptionsAsErrorsToCompileContext() throws IOException, CompilationFailedException {
        IOException anotherException = new IOException("exceptional I/O stuff");

        MockCompilationUnit compilationUnit = new MockCompilationUnit();
        ErrorCollector errorCollector = compilationUnit.getErrorCollector();
        errorCollector.addError(new ExceptionMessage(anotherException, false, null));
        compilationUnit.exception = new MultipleCompilationErrorsException(errorCollector);

        VirtualFile fileToCompile = createVirtualFile("/home/foo/acme/src/bar.groovy");

        Mock mockCompileContext = createMockedCompileContextWithStubbedProgressIndicator();
        mockCompileContext.expects(once()).method("addMessage")
                .with(new Constraint[] { same(CompilerMessageCategory.ERROR), eq(anotherException.getMessage()),
                                         eq(fileToCompile.getUrl()), eq(-1), eq(-1) });

        TranslatingCompiler.ExitStatus exitStatus = compile(fileToCompile, mockCompileContext, compilationUnit);
        assertEquals("number of files to recompile", 1, exitStatus.getFilesToRecompile().length);
        assertSame("file to recompile", fileToCompile, exitStatus.getFilesToRecompile()[0]);

        assertEquals("number of files compiled", 0, exitStatus.getSuccessfullyCompiled().length);

        assertEquals("number of errors", 1, errorCollector.getErrorCount());
        assertSame("exception", anotherException, errorCollector.getException(0));

        assertEquals("number of warnings", 0, errorCollector.getWarningCount());
    }

    public void testCompilesASingleErrorFreeGroovyScriptAndAddsWarningsToCompileContext() throws IOException {
        MockCompilationUnit compilationUnit = new MockCompilationUnit();
        WarningMessage expectedWarningMessage = new WarningMessage(WarningMessage.LIKELY_ERRORS, "a warning", null, null);

        ErrorCollector errorCollector = compilationUnit.getErrorCollector();
        errorCollector.addWarning(expectedWarningMessage);

        VirtualFile fileToCompile = createVirtualFile("/home/foo/acme/src/bar.groovy");

        Mock mockCompileContext = createMockedCompileContextWithStubbedProgressIndicator();
        mockCompileContext.expects(once()).method("addMessage")
                .with(new Constraint[] { same(CompilerMessageCategory.WARNING), eq(expectedWarningMessage.getMessage()),
                                         NULL, eq(-1), eq(-1) });

        TranslatingCompiler.ExitStatus exitStatus = compile(fileToCompile, mockCompileContext, compilationUnit);
        assertEquals("number of files to recompile", 0, exitStatus.getFilesToRecompile().length);

        assertEquals("number of files compiled", 1, exitStatus.getSuccessfullyCompiled().length);
        assertEquals("file compiled", compilationUnit.getConfiguration().getTargetDirectory().getCanonicalPath(),
                     exitStatus.getSuccessfullyCompiled()[0].getOutputPath());

        assertEquals("number of errors", 0, errorCollector.getErrorCount());

        assertEquals("number of warnings", 1, errorCollector.getWarningCount());
        assertSame("warnings", expectedWarningMessage, errorCollector.getWarning(0));
    }

    private TranslatingCompiler.ExitStatus compile(VirtualFile file, Mock mockCompileContext, CompilationUnit compilationUnit) {
        Module module = (Module) mock(Module.class).proxy();
        mockCompileContext.expects(once()).method("getModuleByFile").with(same(file)).will(returnValue(module));

        compilationUnit.getConfiguration().setTargetDirectory("/home/foo/acme/classes");
        mockGroovyController.expects(once()).method("createCompilationUnit").with(same(file), same(module))
                .will(returnValue(compilationUnit));

        return groovyCompiler.compile((CompileContext) mockCompileContext.proxy(), new VirtualFile[] { file });
    }

    private Mock createMockedCompileContextWithStubbedProgressIndicator() {
        Mock mockCompileContext = mock(CompileContext.class);
        mockCompileContext.stubs().method("getProgressIndicator").will(returnValue(new CompilerProgressIndicator(null, false, null)));
        return mockCompileContext;
    }

    private VirtualFile createVirtualFile(String filePath) {
        Mock mockVirtualFile = Mocks.createVirtualFileMock(this);
        mockVirtualFile.stubs().method("getPath").will(returnValue(filePath));
        mockVirtualFile.stubs().method("getUrl").will(returnValue("file://" + filePath));
        return (VirtualFile) mockVirtualFile.proxy();
    }

    private static class MockCompilationUnit extends CompilationUnit {

        private CompilationFailedException exception;

        public MockCompilationUnit() {
            super(new CompilerConfiguration());
        }

        public void compile() throws CompilationFailedException {
            if (exception != null) {
                throw exception;
            }
        }
    }
}
