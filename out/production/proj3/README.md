# Build Your Own World Design Document

**Partner 1:**
Tianyue (Choco) Li
**Partner 2:**
Matt Yan
## Classes and Data Structures
/*
for each class:
definition
instance variables (brief and concise description) 

##################################
Class: Point

A point on canvas

int x, y;

(TETile tile;)

##################################
Class: Room 

int length, width; > 0 == up and right

Point bottomLeft : bottom left corner : will be a wall

Hallway prevH : the entry hallway into this room
##################################
Class: Hallway

int length; length > 0: up and right

boolean orientation (true = vertical)

Point start, end; 

Room prevRoom : the prev room

##################################
Class: Build

TETile[][] world

int size, 

W = Engine.HEIGHT, 

L = Engine.WIDTH

final double ratio = .45

##################################
Class: Player

final int up, down, left, right

final bool vertical, horizontal

final int W, L

##################################
Class: Engine

final WIDTH, HEIGHT

Random rd

TETile[][] wld, playerWld

Point position : of avatar

Point door

boolean doorLocked : state of the game

TETile key : set the key feature to Tile FLOWER

String currentTileName : //TODO, for HUD display

boolean stagedToSave : //TODO, signals a colon was pressed so get ready for save on 'Q'

long seed, int floor, sight, mode

final int up, down, left, right : signaled by "sight"

final int menu, promptScreen, playScreen : signaled by "mode" to know which screen to display

## Algorithms
/*
how your code works
for each class:
high-level description of the methods (include edge cases)
how different classes interact
split task into parts
clearly mark titles or names of classes using white space or other symbols

##################################
CLASS Point

Point(x, y); Constructor

equals(Point); check if two points are at same position

hashCode()

getX()

getY()

getT()

##################################
CLASS Room

Room(length, width, Point) : constructor

makeFirstRoom() : returns a room on a blank canvas, limited to the left

get(side) : get a list of Points on xxx side of the room, no corners

getCorners, getSpace, getPrevRoom;

collectSides : return all Points on four sides, no corners

prevHallwayEntries : return hallway's entrance and its adjacent Points (3 total)

getAllPoint : all point, no removes

getModifiedAllPoint : all points, hallway entries removed, used in check()

create : randomly generate the next hallway

#################################

CLASS Hallway

Hallway(lenght, orientation, Point) : constructor

getIntOf(Point) : if point is start/end, return 3 points (it with adjacents)

getSides(), getSpace(), getAllPoints, 

getModifiedAllPoints : all points without start or adjacents

getAdjacents : get points next to input point

create(Random) : generate new room

#####################################
CLASS Build

putRoom(Room) : set corresponding tiles in world[][], prevHallway's end becomes FLOOR

putHallway(Hallway) : set corresponding tiles in world[][], end is set to WALL

checkPoint() : check if the given point is within the canvas and does not overlap with existing tile

check() : check if the given block can be put

putEnd() : replace the end of the hallway to be a wall, instead of a floor

createValidH() : Create a hallway with current room, if success, return the hallway.
                         If a valid hallway is not created in 100 tries, go back to the previous room
                         and repeat the process. If previous room does not exist, return null.
                         
createValidB() : Firstly, try to create a room with current hallway. If success, return the room.
                         If a valid room is not created in 100 tries, go back to the previous room.
                         Create a valid hallway using previous room by calling createValidH and return it.
                         
makeEmptyWorld()

Build() : constructor

getWorld()

##################################
CLASS Player

getPlayerWld(sight, wld, pos) : returns a 2D tile world that only shows where the player can see. 
                                    Sight changes depending on the direction of movement.

getPoints(sight, pos) : helper function for getPlayerWld that returns the points seen by player.

findSurroundingPoints(pos, surround) : helper function to getPoints that returns 
                                            the direct surrounding points of player position.

makePoints(orientation, start, num) : helper function to getPoints, 
                                            collect a number of points from the start point according to orientation
                                            
##################################
CLASS Engine

interactWithKeyboard() : starts a game with menu, show promptScreen, then start an interactive game.

drawMenu() : helper to IWK, uses StdDraw to show main menu

runMenu(char) : helper to IWK when player interacting with main menu; 
                    if N pressed, draws prompt screen and change mode
                    if L pressed, //TODO, load the saved .txt
                    if Q pressed, //TODO, quit screen
                    
drawPrompt(String) : helper for run-functions that displays message in the center of screen

drawHUD() : draw a bar that displays the tile where the mouse is at, player name and current floor

runPromptScreen(char) : reads seed input and convert to seed (long), display seed on screen while typed;
                            after S is typed, initialize world and place avatar;
                            intialize the renderer, get the player's view, 
                            use Renderer to draw frame
                            //TODO: display HUD
                            change mode to play
                            
initWorld() : uses Build class to initialize the world and place avatar;
                places avatar, key, and door on the map.

generateAvatar, generateDoor, generateKey : helper to initWorld

play(char) : addresses movement command when in play mode;
                moves the avatar, changes playerworld accordingly, 
                
write() : helper when ":Q" is pressed that saves the current stage of game  
                
load() : helper when L is pressed, sets all variables to prev state
                
moveAvatar(char) : Helper to play(), moves avatar according to command, changes sight and player world
                    if player reaches an unlocked door, goes to the next floor, saves current status.
                    
moveMonster() : 

drawGameOver() : clears screen, shows game is over, gives options to return to main screen or quit game.

runGameOver() : takes in Q for quit and R for returning to home screen.

check(Point) : helper to moveAvatar that determines if avatar can move in desired direction;
                also manages key collection and doorLocked status.

generateNextFloor() : helper for advancing, called from moveAvatar;
                        initializes new world

readMouse() : called during each loop in IWK to check mouse position, regardless of whether key is pressed
                if on play mode, changes currentTileName to this tile to be displayed by HUD

interactWithInputString(input) : with an input that 
                                    if starts with N, contains a seed, stops seed at S, 
                                    then contains some movement directions. 
                                    returns state of the world after inputed directions.
                                    if starts with L, 
                                    //TODO, load game from .txt, read()
                                    
getSeed(StringInputDevice) : set seed to what was entered by user

execute(StringInputDevice) : moves avatar or saves game depending on the prompt entered



## Persistence
/*
done after phase 1;
describe how to save state of a world and load it again
include all interacting components (class, methods, files)

We use BufferedWriter and BufferedReader to write and load our world (a 2D-TETile)
and all variables.
*/
