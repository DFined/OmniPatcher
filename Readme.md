# OmniPatcher
Omnipatcher is a modification/skin-changer client for the Valve game Dota 2.

## Description
Omnipatcher is fully automatic, meaning that all the available items for installation are discovered in the game files 
and loaded with little to no moderation or other human influence. This means that all new items will be available 
as soon as they are added to the game. However this also means that there are likely to be both non-fatal and fatal 
errors when installing unusual items or when Valve changes the format in some way. 
Omnipatcher is a non-injecting skin changer. It does not inject any code into Dota dlls or edit any existing game files.
This means that it isn't classified as a cheat by the Valve Anticheat (VAC). Unfortunately this means that not all 
items can be installed perfectly. For example custom animations or effects, which are usually absent may not install 
and are known to be problematic.

## Usage
Warning: Currently in extremely early alpha. use at your own risk. May malfunction or not work at all until recent updates to the game format are accounted for 
To use the patcher, you need to add '-language omnipatcher' to your launch options for Dota 2. Afterwards, 
launch the patcher, select items you want to patch and install them to the desired hero slots. Then click
'Install to game' and launch Dota 2

## Features
- Install the vast majority of available items, even ones that aren't yet publically available, like those
from the collectors cache votes
- Install items to non-standard slots - items can even be installed to different heroes than they were intended for
- Does not inject code or modify existing game files, so is VAC-safe

## Limitations
- Some heroes have slots with missing information. Items cannot be installed to these slots
- Items for heroes with multiple models (such as tiny) are currently unsupported
- Styles don't work for compound items (mainly arcanas)

## License
Parts of this project are under the MIT License. In particular Omnipatcher uses ValveResourceFormat:  
>https://github.com/SteamDatabase/ValveResourceFormat 

and JavaVPK:  
>https://github.com/connorhaigh/JavaVPK

both of which are under the MIT License. The rest of the project is currently under "All Rights Reserved"
