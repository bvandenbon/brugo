# BruGo
Brugo is a java library for the game of go

# Setup
If you use maven, you can configure brugo in your pom.xml as follows.

    <dependencies>
        <dependency>
            <groupId>brugo</groupId>
            <artifactId>brugo</artifactId>
            <version>LATEST</version>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>brugo</id>
            <url>https://github.com/brugo/brugo/raw/repository/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>

# Getting Started

    // Intersection represents a point on the board.
    Intersection is = Intersection.valueOf(x, y);
    
    // An intersection can have a status. A status is one of BLACK/WHITE/EMPTY/EMPKO.
    
    // creates an empty board with 6.5 komi.
    Position p1 = new Position(19, 6.5);
   
    // playing a move on the position will not modify the position, but will create a new one.
    // (captured stones will be removed automatically.)
    Position p2 = p1.play(Intersection.valueOf(16, 3), Status.BLACK);
    
    
