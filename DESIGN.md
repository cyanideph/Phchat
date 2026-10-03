# Phchat Design System

## Direction
Phchat is a mobile-first Filipino social messaging product with a distinctive editorial identity. The interface must feel handcrafted and product-specific, not like a generic AI/vibe-coded social app.

The visual language is informed by the Awesome DESIGN.md reference studies, especially the restraint, hierarchy and surface discipline found in Linear-style product interfaces, combined with social-product energy inspired by Spotify-style content scanning. These are references, not copies.

## Color roles
### Brand palette
- Acid lime: primary action, selection, active states and signature highlights.
- Lavender/periwinkle: secondary surfaces, alternate blocks and supporting actions.
- Mint/teal: positive, online, confirmation and supporting data.
- Graphite-charcoal: dark canvas and elevated surfaces.
- Lavender neutrals: light canvas, borders and light-mode surfaces.

### Rules
- MaterialTheme.colorScheme.primary is acid lime in dark mode and the accessible lavender/periwinkle action color in light mode.
- Do not use primary for decorative headings or passive icons.
- Use semantic error/warning/success colors for status.
- Never rely on color alone to communicate state.
- Brand mark may retain acid lime because the logo is a brand asset, not a UI action.
- Keep backgrounds quiet; decorative Signal Field geometry must stay behind content.

## Typography
Use the Material typography scale defined in ui/theme/Type.kt.

Hierarchy:
1. Headline — screen purpose.
2. Title — section anchor.
3. Body — content.
4. Label — metadata and controls.

Bold is reserved for primary displays, section anchors and active values.

## Spacing
Use PhchatSpacing:
- 4dp xs
- 8dp sm
- 12dp md
- 16dp lg
- 24dp xl
- 32dp xxl
- 48dp display

Avoid one-off spacing values when an existing token fits.

## Shapes and surfaces
Use PhchatShapes and Material theme surfaces.
- Small: compact controls and badges.
- Medium: common interactive surfaces.
- Large: major sheets/containers.
- Avoid wrapping every piece of content in a card.
- Prefer whitespace and hairlines for grouping.
- Elevation is reserved for functional overlays and clearly elevated surfaces.

## Navigation
Primary destinations remain: Rooms, Messages, Feed, Highlights, Profile.
Selected state uses the primary action role. Navigation should remain predictable and mobile-first.

## Content
Product UI is English-first. Filipino identity is expressed through the Phchat brand and community context rather than untranslated navigation chrome.

Avoid emoji as interface icons, decorative slogans, duplicate labels, generic AI terminology, and Uzzap/legacy branding.
Emoji may remain as actual user-generated stickers or reactions.

## Accessibility
- Interactive controls should provide at least a comfortable 48dp touch target.
- Provide content descriptions for meaningful icons.
- Pair color with text, icon, shape or position for important states.
- Preserve readable contrast in both light and dark modes.
- Disabled controls must visibly read as inactive.
- Avoid unnecessary motion.

## Screen composition
Each first view should have one clear primary task and at most one supporting summary.
For browse screens: screen purpose, primary browse/search task, content, then secondary actions.
Creation forms should generally appear after explicit user intent.

## Signature elements
### Signal Field
A subtle geometric backdrop using acid lime, lavender and mint accents. It provides identity without competing with content or controls.
### Phchat Mark
A custom network/chat mark using the acid-lime brand signature and graphite foreground.
### Live Strip
A compact activity strip communicating current room/community activity without becoming a dashboard.

## Implementation rule
Design changes must preserve Supabase contracts, navigation behavior and existing application functionality. A redesign is complete only when the structural change is visible in the rendered UI, not merely in tokens or unused components.