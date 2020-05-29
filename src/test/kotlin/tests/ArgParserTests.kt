package tests

import ArgParser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import java.lang.IllegalArgumentException
import java.lang.RuntimeException


class ArgParserTests {

    var parser = ArgParser()
    var parsedArgs: Map<String, List<String>> = HashMap()

    @AfterEach
    fun after() {
        parser = ArgParser()
    }

    @Test
    fun `no args`() {
        parsedArgs = parser.parse(ArrayList<String>())
        assert(parsedArgs.isEmpty())
        assertEquals(0, parsedArgs.size)
    }

    @Test
    fun `add single non-required arg no body`() {
        parser.addArg("foo", "f", false)
        parsedArgs = parser.parse("-foo".split(" "))
        assert(parsedArgs.containsKey("foo"))
        assertEquals(1, parsedArgs.size)
    }

    @Test
    fun `add single required arg no body`() {
        parser.addArg("foo", "f", true)
        parsedArgs = parser.parse("-foo".split(" "))
        assert(parsedArgs.containsKey("foo"))
        assertEquals(1, parsedArgs.size)
    }

    @Test
    fun `add single non-required arg with body`() {
        parser.addArg("foo", "f", false)
        parsedArgs = parser.parse("-f nate is cool".split(" "))
        assertEquals(1, parsedArgs.size)
        assert(parsedArgs.containsKey("foo"))
        assertEquals("nate is cool".split(" "), parsedArgs["foo"])
    }

    @Test
    fun `add single required arg with body`() {
        parser.addArg("foo", "f", true)
        parsedArgs = parser.parse("-f nate is cool".split(" "))
        assertEquals(1, parsedArgs.size)
        assert(parsedArgs.containsKey("foo"))
        assertEquals("nate is cool".split(" "), parsedArgs["foo"])
    }

    @Test
    fun `add multiple args no body missing non-required`() {
        parser.addArg("foo", "f", true)
        parser.addArg("bar", "b", false)
        parsedArgs = parser.parse("-f".split(" "))
        assertEquals(1, parsedArgs.size)
        assert(parsedArgs.containsKey("foo"))
        assertEquals(ArrayList<String>(), parsedArgs["foo"])
    }

    @Test
    fun `fail unregistered argument one arg`() {
        assertThrows(RuntimeException::class.java) {
            parser.parse("-l".split(" "))
        }
    }

    @Test
    fun `fail unregistered argument multiple arg`() {
        parser.addArg("bar", "b", true)
        assertThrows(RuntimeException::class.java) {
            parser.parse("-f".split(" "))
        }
    }

    @Test
    fun `fail to add duplicate name by name`() {
        parser.addArg("foo", "f", true)
        assertThrows(IllegalArgumentException::class.java) {
            parser.addArg("foo", "g", true)
        }
    }

    @Test
    fun `fail to add duplicate abbrev by abbrev`() {
        parser.addArg("foo", "f", true)
        assertThrows(IllegalArgumentException::class.java) {
            parser.addArg("bar", "f", true)
        }
    }

    @Test
    fun `fail to add duplicate name by abbrev`() {
        parser.addArg("foo", "f", true)
        assertThrows(IllegalArgumentException::class.java) {
            parser.addArg("bazinga", "foo", true)
        }
    }

    @Test
    fun `fail to add duplicate abbrev by name`() {
        parser.addArg("foo", "f", true)
        assertThrows(IllegalArgumentException::class.java) {
            parser.addArg("f", "bazinga", true)
        }
    }

    @Test
    fun `fail to add required arg no body`() {
        parser.addArg("foo", "f", true)
        parser.addArg("bar", "b", false)
        assertThrows(IllegalArgumentException::class.java) {
            parser.parse("-bar".split(" "))
        }
    }

    @Test
    fun `fail to add required arg with body`() {
        parser.addArg("foo", "f", true)
        parser.addArg("bar", "b", false)
        assertThrows(IllegalArgumentException::class.java) {
            parser.parse("-bar lol nate".split(" "))
        }
    }

    @Test
    fun `add multiple required args with no bodies`() {
        parser.addArg("foo", "f", true)
        parser.addArg("bar", "b", true)
        parsedArgs = parser.parse("-bar -foo".split(" "))
        assert(parsedArgs.containsKey("bar"))
        assert(parsedArgs.containsKey("foo"))
        assertEquals(ArrayList<String>(), parsedArgs["foo"])
        assertEquals(ArrayList<String>(), parsedArgs["bar"])
    }

    @Test
    fun `add multiple non-required args with no bodies`() {
        parser.addArg("foo", "f", false)
        parser.addArg("bar", "b", false)
        parsedArgs = parser.parse("-bar -foo".split(" "))
        assert(parsedArgs.containsKey("bar"))
        assert(parsedArgs.containsKey("foo"))
        assertEquals(ArrayList<String>(), parsedArgs["foo"])
        assertEquals(ArrayList<String>(), parsedArgs["bar"])
    }

    @Test
    fun `add multiple args with varying bodies`() {
        parser.addArg("foo", "f", true)
        parser.addArg("bar", "b", true)
        parser.addArg("far", "a", false)
        parsedArgs = parser.parse("-far nate is cool -foo lol man -bar coolio".split(" "))

        assertEquals(3, parsedArgs.size)
        assert(parsedArgs.containsKey("bar"))
        assert(parsedArgs.containsKey("foo"))
        assert(parsedArgs.containsKey("far"))
        assertEquals("nate is cool".split(" "), parsedArgs["far"])
        assertEquals("lol man".split(" "), parsedArgs["foo"])
        assertEquals("coolio".split(" "), parsedArgs["bar"])
    }

    @Test
    fun `add multiple args with varying bodies missing non-required`() {
        parser.addArg("foo", "f", true)
        parser.addArg("bar", "b", true)
        parser.addArg("far", "a", true)
        parser.addArg("lol", "l", false)
        parser.addArg("pop", "p", false)
        parsedArgs = parser.parse("-far nate is cool -foo lol man -bar coolio -p i u o".split(" "))

        assertEquals(4, parsedArgs.size)
        assert(parsedArgs.containsKey("bar"))
        assert(parsedArgs.containsKey("foo"))
        assert(parsedArgs.containsKey("far"))
        assert(parsedArgs.containsKey("pop"))
        assertEquals("nate is cool".split(" "), parsedArgs["far"])
        assertEquals("lol man".split(" "), parsedArgs["foo"])
        assertEquals("coolio".split(" "), parsedArgs["bar"])
        assertEquals("i u o".split(" "), parsedArgs["pop"])
    }

}