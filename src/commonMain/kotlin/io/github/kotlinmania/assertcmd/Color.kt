// port-lint: source assert_cmd/src/color.rs
package io.github.kotlinmania.assertcmd

import ai.solace.tui.anstyle.AnsiColor
import ai.solace.tui.anstyle.Effects
import ai.solace.tui.anstyle.Style
import ai.solace.tui.anstyle.or

/**
 * Compile-time toggle corresponding to the upstream Rust `feature = "color"` Cargo feature.
 * When `true`, [Palette.color] returns a palette with the same ANSI styling as the Rust crate;
 * when `false`, it falls back to [Palette.plain].
 */
internal const val COLOR_FEATURE: Boolean = true

internal data class Palette(
    val key: Style = Style(),
    val value: Style = Style(),
) {
    internal fun key(display: Any): Styled = Styled(display, key)

    internal fun value(display: Any): Styled = Styled(display, value)

    internal companion object {
        internal fun new(key: Style = Style(), value: Style = Style()): Palette = Palette(key, value)

        internal fun color(): Palette =
            if (COLOR_FEATURE) {
                Palette(
                    key = AnsiColor.Blue.onDefault() or Effects.BOLD,
                    value = AnsiColor.Yellow.onDefault() or Effects.BOLD,
                )
            } else {
                plain()
            }

        internal fun plain(): Palette = Palette()
    }
}

internal class Styled(
    private val display: Any,
    private val style: Style,
) {
    /**
     * Render the inner display without any ANSI styling. Mirrors the non-alternate branch of
     * the upstream Rust `Display::fmt` implementation (`{}`).
     */
    override fun toString(): String = display.toString()

    /**
     * Render the inner display surrounded by the style's ANSI introducer and reset codes.
     * Mirrors the alternate branch of the upstream Rust `Display::fmt` implementation (`{:#}`).
     */
    internal fun renderStyled(): String =
        buildString {
            style.render().formatTo(this)
            append(display.toString())
            style.renderReset().formatTo(this)
        }

    internal fun fmt(alternate: Boolean = false): String =
        if (alternate) {
            renderStyled()
        } else {
            toString()
        }

    internal companion object {
        internal fun new(display: Any, style: Style): Styled = Styled(display, style)
    }
}
