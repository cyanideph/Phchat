# Phchat icon system

Phchat uses a two-layer icon language so the UI stays distinctive without making small controls heavy.

## 1. Hero / feature visuals

Use **3dicons V1** for prominent visual moments:

- empty chat / conversation states
- new conversation
- profile / people
- media and camera
- notifications
- calls
- community / discovery
- onboarding and feature cards

The V1 collection contains 122 icons / 1464 renders and is published as CC0. Source: https://3dicons.co/collection/b68cf8-v1

Preferred treatment for Phchat:
- black / near-black surfaces
- restrained use of the 3dicons color style
- consistent camera angle within a screen
- no mixing multiple 3d camera angles in one composition
- 3D artwork is decorative/supporting content, never the only way to understand an action

Recommended source icons include: Chat, Chat bubble, Mobile, Camera, Bell, Mic, Video cam, Call ringing, Boy/Girl, Zoom, Setting, Lock, Picture, Folder, Heart, Bookmark, Location, Gift, Trophy, Fire.

## 2. Micro interaction icons

Use the existing local AndroidX Material Icons Extended set for:
- back / close
- send
- attach
- overflow
- search
- navigation
- play/pause
- check
- small status indicators

These remain vector-based so controls are crisp, accessible, fast, and do not require network access.

## 3. Consistency rules

1. Do not mix unrelated icon packs on the same screen.
2. Do not use 3D icons as tiny 16–24dp action icons.
3. Use one 3D camera angle per visual group.
4. Keep 3D assets at deliberate sizes (typically 72–180dp).
5. Use semantic content descriptions for all interactive icons.
6. Prefer local bundled assets for production builds; do not make core navigation dependent on a remote CDN.
7. Keep the black Phchat theme dominant. Accent colors are supporting details, not the background system.
8. New icons must be added to the centralized Phchat vocabulary before being used by screens.

## Licensing

The original 3dicons V1 collection is described by the project as CC0. The source repository is also published under CC0-1.0.

- 3dicons: https://3dicons.co/
- V1 collection: https://3dicons.co/collection/b68cf8-v1
- Source repository: https://github.com/realvjy/3dicons
- Figma plugin source: https://github.com/realvjy/3dicons-figma

Before adding a new asset from another icon library, verify its license separately. Do not assume another Figma Community asset is CC0 just because 3dicons is.
