package org.codehaus.groovy.runtime

class GroovyCategoryTest extends GroovyTestCase {
	void testUseWithVarArg() {
		// Make sure I didn't break use(Category, Closure)
		use (Category1) {
			assert "HeLlO".upper() == "HELLO"
		}

        // Make sure I didn't break use([Category, Category], Closure)
        use ([Category1, Category2]) {
            assert "HeLlO".upper() == "HELLO"
            assert "HeLlO".lower() == "hello"
        }
	
		// Try out the new vararg version implemented in DGM
		use (Category1, Category2) {
			assert "HeLlO".upper() == "HELLO"
			assert "HeLlO".lower() == "hello"
		}
		
		// This should fail
		try {
			use (Category1)
			fail()
		} catch (IllegalArgumentException e) {
		}
		
		// And so should this
		try {
			use (Category1, Category2)
			fail()
		} catch (IllegalArgumentException e) {
		}
	}
}

class Category1 {
	static String upper(String message) { return message.toUpperCase() }
}

class Category2 {
	static String lower(String message) { return message.toLowerCase() }
}