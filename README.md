# BruGo
Brugo is a java library for the game of go

# Setup
If you use maven, you can configure brugo in your `pom.xml` as follows.

    <dependencies>
        <dependency>
            <groupId>be.brugo</groupId>
            <artifactId>brugo</artifactId>
            <version>LATEST</version>
        </dependency>
    </dependencies>

# Getting Started with Positions

    // Intersection represents a point on the board.
    Intersection is = Intersection.valueOf(x, y);
    
    // An intersection can have a status. A status is one of BLACK/WHITE/EMPTY/EMPKO.
    
    // creates an empty board with 6.5 komi.
    Position p1 = new Position(19, 6.5);
   
    // playing a move on the position will not modify the position, but will create a new one.
    // (captured stones will be removed automatically.)
    Position p2 = p1.play(Intersection.valueOf(16, 3), Status.BLACK);
    
# Getting started with JavaFx

   Launch brugo.go.ui.javafx.goban.GobanComponentDemo for a demo.
   
   
    
