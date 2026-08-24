// port-lint: tests color.rs
package io.github.kotlinmania.assertcmd

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorTest {
    @Test
    fun testPalettePlain() {
        val plain = Palette.plain()
        val styledKey = plain.key("key")
        val styledVal = plain.value("val")

        assertEquals("key", styledKey.toString())
        assertEquals("val", styledVal.toString())
        assertEquals("key", styledKey.fmt(alternate = false))
    }

    @Test
    fun testPaletteColor() {
        val color = Palette.color()
        val styledKey = color.key("testkey")
        assertEquals("testkey", styledKey.toString())
        assertTrue(styledKey.renderStyled().contains("testkey"))
    }
}
