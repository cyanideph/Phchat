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

## Premium Editorial Extension

# Phchat Premium Editorial Design System

> Implementation reference for the native Android/Jetpack Compose product. This system adapts premium fintech/editorial visual language to social messaging; it does not turn Phchat into a finance product.

## Direction
**Mood:** sleek, premium, editorial, tactile, social, Filipino.
**Primary product moment:** people, rooms, conversations, presence, compose/send.
**Visual rule:** deep graphite canvas + large flat acid-lime, lavender, and mint surfaces. The accent palette creates hierarchy; it does not decorate every component.

Closest reference languages: Revolut for high-contrast editorial fintech composition and Slack for information-dense social communication patterns. These are references only; Phchat keeps its own product identity.

## Color tokens
### Dark
| Token | Value | Role |
|---|---|---|
| graphite | #16161A | App background |
| surface | #1C1C20 | Cards and primary surfaces |
| surface-raised | #24242A | Inputs, secondary cards, sheets |
| surface-soft | #2B2B32 | Hover/pressed/inset regions |
| hairline | #34343B | Borders/dividers |
| text | #F2F2F5 | Primary text |
| text-secondary | #8A8A93 | Metadata/supporting text |
| lime | #D7F542 | Primary action/active/highlight |
| lime-bright | #CCF52C | High-energy active treatment |
| lavender | #A78BFA | Secondary blocks and selected secondary states |
| periwinkle | #8B7CF6 | Lavender family support |
| mint | #3ECF8E | Presence/success/support action |
| teal | #2FD6A3 | Secondary data/activity |
| black | #0A0A0C | Text on bright accent surfaces |
| red | #FF6B6B | Destructive/error only |
| amber | #F5C451 | Attention/away only |

### Light
Light mode uses an off-white editorial canvas rather than blue-white defaults.
- Background: #F5F5F2
- Surface: #FFFFFF
- Surface variant: #E8E8E4
- Primary action: darkened lime #657A00 with black text
- Secondary: darkened lavender #6548C5
- Support: darkened mint #14784F
- Primary text: #101014
- Secondary text: #5F6068

## Color usage
1. One leading action color: acid lime. Use it for compose/send, active navigation, key confirmation and important highlights.
2. Never use lime as generic decoration. If everything is lime, nothing is primary.
3. Lavender creates secondary grouping: discovery, profile/community modules, alternate content blocks.
4. Mint/teal communicates presence, successful completion and supportive activity.
5. Red/amber retain semantic meaning and never become brand accents.
6. Never put muted gray text on bright colored surfaces. Use an intentional dark foreground.
7. Color is never the only status cue; pair it with icon, text, shape or position.

## Typography
Use the Android system sans for reliability and native rendering.
- Display: 34sp / bold / tight tracking
- Headline: 28sp / bold
- Section: 22sp / bold
- Title: 18sp / semibold
- Body: 16sp / regular
- Secondary body: 14sp / regular
- Labels: 12–14sp / medium-semibold
- Metadata: 11sp / medium
Reserve the largest type for the screen's purpose, not the app logo.

## Shape language
- Cards: 20dp
- Secondary cards: 16dp
- Inputs: 14–16dp
- Compact badges: 8dp
- Avatars: circular
- Primary action buttons: 16–20dp, minimum 48dp height
- Avoid turning every element into a pill.

## Spacing
Base rhythm: 4dp, with primary steps 8 / 12 / 16 / 24 / 32dp.
- 8dp: icon-to-label and compact metadata
- 12dp: internal row spacing
- 16dp: standard component padding
- 24dp: section separation
- 32dp: major screen groups

## Messaging composition
### Conversation list
The first view should immediately answer: who is active/relevant, what was the latest message, and what needs attention. Use strong unread hierarchy, compact presence, and one clear compose affordance.
### Message thread
- Incoming bubbles use surface-raised.
- Outgoing messages use acid lime with black text.
- Reply/mention/reaction controls use restrained surface variants.
- Do not use oversized floating cards around every message.
- Keep composer attached to the message flow and visually dominant only while composing.
### Tambayan rooms
Room cards should expose room identity, province/region, online presence and latest activity without becoming mini dashboards.
### Navigation
Use bottom navigation for primary destinations. Active item receives the strongest accent treatment; inactive items remain quiet.

## Surface/depth
Prefer flat color blocks and hairlines over generic glassmorphism.
Allowed: subtle tonal elevation, 1dp hairlines, restrained shadows for sheets/dialogs, soft pastel textile/fabric imagery for editorial onboarding/marketing moments.
Avoid: permanent blur/glass panels, excessive gradients, glowing neon outlines, equal-elevation card grids, decorative chart chrome.

## Activity visualizations
When a chart is useful, it represents social activity rather than finance: lime → yellow-green for primary activity; muted gray-green for comparison; show labels/values directly where useful; never invent a trend or status.

## Accessibility
- Minimum 48dp interactive targets.
- Maintain WCAG-appropriate contrast in actual rendered states.
- Do not rely on color alone for online/away/error.
- Support system font scaling.
- Preserve clear focus/pressed states.
- Avoid tiny metadata as the only way to identify an action.

## Iconography
Current app uses Material Icons Extended. Keep one icon family and one visual weight across navigation and controls. Do not use emoji as interface icons; emoji remain valid as user-generated sticker/reaction content.

## Do not
- Do not revert to blue-primary branding.
- Do not use ecommerce/portfolio/financial terminology in the product UI.
- Do not make the interface look like an AI agent/chatbot.
- Do not create a generic dashboard + equal cards + sidebar shell.
- Do not add decorative charts when messaging/rooms are the task.
- Do not use gradients merely to make the UI look premium.
- Do not put muted gray text on lime, mint or lavender blocks.

## Review gates
1. Compare the same states before/after at mobile width.
2. Verify dark and light mode.
3. Verify auth, home, room chat, direct chat, profile, settings and notifications.
4. Check 48dp touch targets and text scaling.
5. Check for hardcoded legacy blue/green theme values.
6. Check icon consistency and remove emoji-as-icons from chrome.
7. Run build/CI and Android runtime checks.
8. Capture visual evidence before declaring the redesign complete.