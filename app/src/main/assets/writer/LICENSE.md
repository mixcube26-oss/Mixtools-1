# MixTools Document Writer Asset Documentation

This asset bundle contains the self-contained, offline-first Rich Text Document Editor surface for MixTools.

## Components
1. `document_editor.html`: Pure HTML5, CSS3, and JavaScript document editing engine built specifically for MixTools.
- **License**: MIT License / MixTools Open Engine.
- **Dependencies**: 100% self-contained. No external CDN scripts, remote fonts, or network requests are executed at runtime. All styles and pagination rules run locally within Android WebView.
- **Security**: Local sandboxed asset loaded via Android WebKit runtime.

## Capabilities
- Real page rendering with standard sheet aspect ratios (A4, A5, Letter, Legal, Custom).
- Direct tap-to-type WYSIWYG rich text surface.
- Full formatting: Bold, Italic, Underline, Strikethrough, Subscript, Superscript, Font family, Size, Color, Highlight.
- Paragraph layout: Alignment, Lists, Headings, Line spacing, Indent.
- Insert objects: Responsive Tables, Local Images, Horizontal rules, Page breaks, Date/Time.
- Search and Replace.
- Live pagination & stats counter.
