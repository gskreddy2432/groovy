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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.intellij.compiler.impl.javaCompiler.OutputItemImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.ProcessingUnit;
import org.codehaus.groovy.control.messages.WarningMessage;
import org.codehaus.groovy.syntax.SyntaxException;

import org.codehaus.groovy.intellij.GroovyController;
import org.codehaus.groovy.intellij.GroovySupportLoader;

import antlr.RecognitionException;

public class GroovyCompiler implements TranslatingCompiler {

    private final GroovyController controller;

    public GroovyCompiler(GroovyController controller) {
        this.controller = controller;
    }

    public String getDescription() {
        return "Groovy Compiler";
    }

    public boolean isCompilableFile(VirtualFile file, CompileContext context) {
        return GroovySupportLoader.GROOVY == file.getFileType();
    }

    public ExitStatus compile(final CompileContext context, final VirtualFile[] filesToCompile) {
        final List compiledFiles = new ArrayList();
        final List filesToRecompile = new ArrayList();

        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                compile(context, filesToCompile, compiledFiles, filesToRecompile);
            }
        });

        return new ExitStatusImpl((OutputItem[]) compiledFiles.toArray(TranslatingCompiler.EMPTY_OUTPUT_ITEM_ARRAY),
                                  (VirtualFile[]) filesToRecompile.toArray(VirtualFile.EMPTY_ARRAY));
    }

    private void compile(CompileContext context, VirtualFile[] filesToCompile, List compiledFiles, List filesToRecompile) {
        for (int i = 0; i < filesToCompile.length; i++) {
            if (context.getProgressIndicator().isCanceled()) {
                break;
            }

            context.getProgressIndicator().setFraction((double) i / (double) filesToCompile.length);
            compile(context, filesToCompile[i], compiledFiles, filesToRecompile);
        }
    }

    private void compile(CompileContext context, VirtualFile fileToCompile, List compiledFiles, List filesToRecompile) {
        Module module = context.getModuleByFile(fileToCompile);
        CompilationUnit compilationUnit = controller.createCompilationUnit(fileToCompile, module);
        compilationUnit.addSource(new File(fileToCompile.getPath()));

        try {
            compilationUnit.compile();
            addCompiledFile(fileToCompile, compiledFiles, compilationUnit.getConfiguration().getTargetDirectory());
        } catch (Exception e) {
            processCompilationException(e, fileToCompile, filesToRecompile, context);
        } finally {
            addWarnings(compilationUnit, context);
        }
    }

    private void addCompiledFile(VirtualFile compiledFile, List compiledFiles, File targetDirectory) throws IOException {
        String outputRootDirectory = targetDirectory.getParentFile().getCanonicalPath();
        String outputPath = targetDirectory.getCanonicalPath();
        compiledFiles.add(new OutputItemImpl(outputRootDirectory, outputPath, compiledFile));
    }

    private void addWarnings(CompilationUnit compilationUnit, CompileContext context) {
        for (int i = 0; i < compilationUnit.getWarningCount(); i++) {
            WarningMessage warning = compilationUnit.getWarning(i);
            context.addMessage(CompilerMessageCategory.WARNING, warning.getMessage(), null, -1, -1);
        }
    }

    private void processCompilationException(Exception exception, VirtualFile fileToCompile, List filesToRecompile, CompileContext context) {
        if (exception instanceof CompilationFailedException) {
            CompilationFailedException compilationFailureException = (CompilationFailedException) exception;
            ProcessingUnit unit = compilationFailureException.getUnit();
            for (int i = 0; i < unit.getErrorCount(); i++) {
                processException(unit.getException(i), context, filesToRecompile, fileToCompile);
            }
        } else {
            processException(exception, context, filesToRecompile, fileToCompile);
        }
    }

    private void processException(Exception exception, CompileContext context, List filesToRecompile, VirtualFile fileToCompile) {
        int line = -1;
        int column = -1;

        if (exception instanceof SyntaxException) {
            SyntaxException syntaxException = (SyntaxException) exception;
            line = syntaxException.getLine();
            column = syntaxException.getStartColumn();
        }

        if (exception instanceof RecognitionException) {
            RecognitionException syntaxException = (RecognitionException) exception;
            line = syntaxException.getLine();
            column = syntaxException.getColumn();
        }

        context.addMessage(CompilerMessageCategory.ERROR, exception.getMessage(), fileToCompile.getUrl(), line, column);
        filesToRecompile.add(fileToCompile);
    }

    public boolean validateConfiguration(CompileScope scope) {
        return true;
    }

    private static class ExitStatusImpl implements TranslatingCompiler.ExitStatus {

        private OutputItem[] compiledFiles;
        private VirtualFile[] filesToRecompile;

        public ExitStatusImpl(OutputItem[] compiledFiles, VirtualFile[] filesToRecompile) {
            this.compiledFiles = compiledFiles;
            this.filesToRecompile = filesToRecompile;
        }

        public TranslatingCompiler.OutputItem[] getSuccessfullyCompiled() {
            return compiledFiles;
        }

        public VirtualFile[] getFilesToRecompile() {
            return filesToRecompile;
        }
    }
}