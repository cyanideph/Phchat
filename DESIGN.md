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