package brugo.go.io.sgf;

import brugo.common.util.FileUtil;
import brugo.go.bo.tree.Game;
import brugo.go.bo.tree.PositionNode;
import brugo.go.bo.state.Intersection;
import brugo.go.bo.state.Move;
import brugo.go.bo.state.Position;
import brugo.go.bo.state.Status;
import brugo.common.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SGFReader {
    public Pair<Game[], List<String>> readGames(File pFile) {
        SGFFileNode fileNode = readSGFNodeTree(pFile, null);
        List gameNodeList = fileNode.getNextNodeList();
        if (gameNodeList == null) {
            return new Pair<>(null, fileNode.getWarningList());
        }

        Game[] games = new Game[gameNodeList.size()];
        SGFNode gmNode;
        for (int i = 0, iSize = gameNodeList.size(); i < iSize; i++) {
            gmNode = (SGFNode) gameNodeList.get(i);

            // file must contain GM[1] specification
            if (!"1".equals(gmNode.getStringProperty(fileNode, "GM", "1", false))) {
                fileNode.addWarning("Missing GM[1] property");
            }

            games[i] = createGameFromNode(fileNode, gmNode);
        }

        // return multiple values
        return new Pair<>(games, fileNode.getWarningList());
    }

    public Game createGameFromNode(SGFFileNode fileNode) {
      return createGameFromNode(fileNode, fileNode);
    }

    private Game createGameFromNode(SGFFileNode fileNode, SGFNode gmNode) {
        // remove already processed properties
        gmNode.removeProperties("GM", "CA");

        // remove irrelevant properties
        gmNode.removeProperties("FF","AP","US","AN","SD","CP","GC","ID",
            "GN","CH","PL","GK","TE","M","HD","LT","LC","MULTIGOGM","RG","GAME","GOWRITEVERSION");

        // remove unimplemented properties
        gmNode.removeProperty("ST");

        // deprecated tags (supported by SGB format but not by SGF)
        gmNode.removeProperties("CH","SI","SE","LT","BS","WS","TC","ID","OM","OP","CI","OV","RG","SC");

        // Filenode contains log ; GmNode contains data.
        //String  = gmNode.getStringProperty(fileNode, "ST", null, true);
        Integer size = gmNode.getIntegerProperty(fileNode, "SZ", Integer.valueOf(19), true);
        size = gmNode.getIntegerProperty(fileNode, "SIZE", size, true);

        Double komi;
        String strKomi = "6.5";
        strKomi = gmNode.getStringProperty(fileNode, "KM", strKomi, true);
        strKomi = gmNode.getStringProperty(fileNode, "KO", strKomi, true);
        strKomi = gmNode.getStringProperty(fileNode, "KOMI", strKomi, true);
        strKomi = gmNode.getStringProperty(fileNode, "GKM", strKomi, true);
        if (Pattern.compile("\\d+(\\.\\d+)?").matcher(strKomi).matches()) {
            komi = Double.valueOf(strKomi);
        } else {
            if (strKomi.contains("5??")) komi = Double.valueOf(5.5);
            else if (strKomi.contains("6??")) komi = Double.valueOf(6.5);
            else if (strKomi.contains("7??")) komi = Double.valueOf(7.5);
            else komi = Double.valueOf(6.5);
        }

        String rules = gmNode.getStringProperty(fileNode, "RU", null, true);
        String playerBlack = gmNode.getStringProperty(fileNode, "PB", null, true);
        playerBlack = gmNode.getStringProperty(fileNode, "PLAYERBLACK", playerBlack, true);
        String playerWhite = gmNode.getStringProperty(fileNode, "PW", null, true);
        playerWhite = gmNode.getStringProperty(fileNode, "PLAYERWHITE", playerWhite, true);
        String playerX = gmNode.getStringProperty(fileNode, "PX", null, true);
        String playerY = gmNode.getStringProperty(fileNode, "PY", null, true);
        String whiteRank = gmNode.getStringProperty(fileNode, "WR", null, true);
        whiteRank = gmNode.getStringProperty(fileNode, "WHITERANK", whiteRank, true);
        String blackRank = gmNode.getStringProperty(fileNode, "BR", null, true);
        blackRank = gmNode.getStringProperty(fileNode, "BLACKRANK", blackRank, true);
        String date = gmNode.getStringProperty(fileNode, "DT", null, true);
        date = gmNode.getStringProperty(fileNode, "DATE", date, true);
        date = gmNode.getStringProperty(fileNode, "RD", date, true);
        String event = gmNode.getStringProperty(fileNode, "EV", null, true);
        event = gmNode.getStringProperty(fileNode, "EVENT", event, true);
        String result = gmNode.getStringProperty(fileNode, "RE", null, true);
        result = gmNode.getStringProperty(fileNode, "RESULT", result, true);
        result = gmNode.getStringProperty(fileNode, "ER", result, true);
        String dateX = gmNode.getStringProperty(fileNode, "DTX", null, true);
        String source = gmNode.getStringProperty(fileNode, "SO", null, true);
        source = gmNode.getStringProperty(fileNode, "SOURCE", source, true);
        String time = gmNode.getStringProperty(fileNode, "TM", null, true);
        String overtime = gmNode.getStringProperty(fileNode, "OT", null, true);
        String round = gmNode.getStringProperty(fileNode, "RO", null, true);
        round = gmNode.getStringProperty(fileNode, "ROUND", round, true);
        String place = gmNode.getStringProperty(fileNode, "PC", null, true);
        place = gmNode.getStringProperty(fileNode, "PLACE", place, true);
        String blackTeam = gmNode.getStringProperty(fileNode, "BT", null, true);
        String whiteTeam = gmNode.getStringProperty(fileNode, "WT", null, true);
        String handicap = gmNode.getStringProperty(fileNode, "HA", null, true);
        String handicap2 = gmNode.getStringProperty(fileNode, "OH", null, true);
        String whiteCountry = gmNode.getStringProperty(fileNode, "WC", null, true);
        String blackCountry = gmNode.getStringProperty(fileNode, "BC", null, true);
        String timeMaybe = gmNode.getStringProperty(fileNode, "TI", null, true);

        Position startPosition = new Position(size.intValue(), komi.doubleValue());
        PositionNode rootPositionNode = createPositionNodeFromNode(fileNode, null, startPosition, gmNode);

        Game game = new Game(rootPositionNode);
        game.setFilename(fileNode.getFilename());
        game.setRules(rules);
        game.setPlayerWhite(playerWhite);
        game.setPlayerBlack(playerBlack);
        game.setWhiteRank(whiteRank);
        game.setBlackRank(blackRank);
        game.setDate(date);
        game.setEvent(event);
        game.setResult(result);

        return game;
    }

    private PositionNode createPositionNodeFromNode(SGFFileNode pFileNode, PositionNode pParentNodePosition,
                                                    Position pParentPosition,
                                                    SGFNode pSGFNode) {
        try {
            // required fields
            if (pFileNode == null) return null;
            if (pSGFNode == null) return null;
            if (pParentPosition == null) return null;

            boolean blackMove = false;
            boolean whiteMove = false;
            List<Intersection> moveList = new ArrayList<>();
            PositionNode currentNode;
            Position position = pParentPosition;
            String comments = null;

            Map<String, String> attributeMap = pSGFNode.getMap();
            if (attributeMap != null) {
                String value;
                List<Intersection> pointList;
                for (String keyName : attributeMap.keySet()) {
                    value = attributeMap.get(keyName);
                    if ("B".equalsIgnoreCase(keyName) || "BLACK".equalsIgnoreCase(keyName)) {
                        pointList = getPointList(value, position.getSize());
                        position = position.addStoneList(pointList, Status.BLACK);
                        moveList.addAll(pointList);
                        blackMove = true;
                    } else if ("W".equalsIgnoreCase(keyName) || "WHITE".equalsIgnoreCase(keyName)) {
                        pointList = getPointList(value, position.getSize());
                        position = position.addStoneList(pointList, Status.WHITE);
                        moveList.addAll(pointList);
                        whiteMove = true;
                    } else if ("AB".equalsIgnoreCase(keyName)) {
                        pointList = getPointList(value, position.getSize());
                        position = position.addStoneList(pointList, Status.BLACK);
                    } else if ("AW".equalsIgnoreCase(keyName)) {
                        pointList = getPointList(value, position.getSize());
                        position = position.addStoneList(pointList, Status.WHITE);
                    } else if ("C".equalsIgnoreCase(keyName) || "CO".equalsIgnoreCase(keyName) || "COMMENT".equalsIgnoreCase(keyName)) {
                        comments = value;
                    } else if ("LB".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("BL".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("WL".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("JC".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("TR".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("L".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("SQ".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("TW".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("TB".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("AE".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("OB".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("OW".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("RN".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("PT".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("ｃ".equalsIgnoreCase(keyName)) {
                        //todo not supported
                    } else if ("DT".equalsIgnoreCase(keyName)) {
                        //todo not supported here
                    } else {
                        pFileNode.addWarning("File Property Error - Unknown property '" + keyName + "' with value '" + value + "'.");
                    }
                }
            }

            // conclude move color
            Status moveColor = null;
            if (blackMove && whiteMove) moveColor = null;
            else if (blackMove) moveColor = Status.BLACK;
            else if (whiteMove) moveColor = Status.WHITE;

            // conclude move point, (not used for AB and AW properties, only for B and W)
            Intersection movePoint = (moveList.size() == 1) ? moveList.get(0) : null;

            // create (and if possible: also link) the next node
            Move move = new Move(movePoint, moveColor);
            if (pParentNodePosition != null) {
                currentNode = pParentNodePosition.addNextPosition(position, move);
            } else {
                currentNode = new PositionNode(null, position, move);
            }
            currentNode.setComments(comments);

            // add continuations
            List<SGFNode> nextNodeList = pSGFNode.getNextNodeList();
            if (nextNodeList != null)
              for (SGFNode nextNode : nextNodeList)
                createPositionNodeFromNode(pFileNode, currentNode, position.clone(), nextNode);

            return currentNode;
        } catch (Exception ignored) {
        }
        return null;
    }

    @SuppressWarnings("TailRecursion")
    public SGFFileNode readSGFNodeTree(File pFile, String pEncoding) {
        try {
            String fileContent = FileUtil.readFully(pFile, pEncoding);

            // fix common file formatting issues
            fileContent = fixFileFormatIssue(fileContent);

            fileContent = fileContent.trim();
            char[] array = fileContent.toCharArray();
            if (array == null) return null;

            SGFFileNode fileNode = new SGFFileNode(pFile.getName());

            boolean isInError = false;
            boolean readingFirstPropertyNameChar = false;
            boolean readingPropertyName = false;
            boolean firstPropertyInVariation = true;
            String propertyName = "";
            String prevPropertyName = null;
            boolean readingPropertyValue = false;
            String propertyValue = "";
            List<SGFNode> keyNodeHistoryList = new ArrayList<>(50);
            SGFNode currentNode = fileNode;
            char character;
            int line = 1;
            int col = 0;

            for (int i = 0, iLength = array.length; i < iLength; i++) {
                col++;
                character = array[i];

                if (character == '\\') {
                    i++;
                    if (readingPropertyValue) {
                        // next character is special character of text
                        character = array[i];
                        propertyValue += character;
                    }
                } else if (!readingPropertyValue && (character == '(')) {
                    // add previous node as branch keypoint
                    keyNodeHistoryList.add(0, currentNode);
                    firstPropertyInVariation = true;
                } else if (!readingPropertyValue && (character == ')')) {
                    // now continue from the previous keypoint, and remove it from the history list
                    currentNode = keyNodeHistoryList.remove(0);
                    firstPropertyInVariation = false;
                } else if (!readingPropertyValue && (character == ';')) {
                    currentNode = new SGFNode(currentNode);
                    if (currentNode.getParentNode() != null) currentNode.getParentNode().addNextNode(currentNode);
                    firstPropertyInVariation = false;
                    readingPropertyName = true;
                    readingFirstPropertyNameChar = true;
                    prevPropertyName = null;
                } else if (!readingPropertyValue && (character == '[')) {
                    firstPropertyInVariation = false;
                    readingPropertyValue = true;
                    readingPropertyName = false;
                    readingFirstPropertyNameChar = false;
                } else if (readingPropertyValue && (character == ']')) {
                    firstPropertyInVariation = false;
                    if ("".equals(propertyName) && prevPropertyName != null) {
                        propertyName = prevPropertyName;
                        String prevValue = currentNode.getProperty(propertyName.trim().toUpperCase());
                        if (prevValue == null) prevValue = "";
                        propertyValue = prevValue + "," + propertyValue;
                        currentNode.putProperty(propertyName.trim().toUpperCase(), propertyValue);
                    } else {
                        currentNode.putProperty(propertyName.trim().toUpperCase(), propertyValue);
                    }

                    // AHA !!! found an encoding specification, restart with the correct encoding.
                    if ((pEncoding == null) && "CA".equalsIgnoreCase(propertyName.trim().toUpperCase())) {
                        return readSGFNodeTree(pFile, propertyValue);
                    }

                    if ((pEncoding == null) && "KO".equalsIgnoreCase(propertyName.trim().toUpperCase())) {
                        byte[] bytes = {-70, -38, -52, -7, 54, -60, -65, -80, -21};
                        if (Arrays.equals(bytes, propertyValue.getBytes())) {
                            return readSGFNodeTree(pFile, "GBK");
                        }
                    }

                    if ((pEncoding == null) && (
                            "PB".equalsIgnoreCase(propertyName.trim().toUpperCase()) ||
                                    "PW".equalsIgnoreCase(propertyName.trim().toUpperCase()) ||
                                    "BR".equalsIgnoreCase(propertyName.trim().toUpperCase()) ||
                                    "WR".equalsIgnoreCase(propertyName.trim().toUpperCase()))) {
                        byte[] bytes = propertyValue.getBytes();
                        // check for the kanji of "dan" mistakenly read in the wrong encoding.
                        if (bytes.length > 2 && bytes[bytes.length - 2] == -74 && bytes[bytes.length - 1] == -50) {
                            return readSGFNodeTree(pFile, "GBK");
                        }
                    }

                    if ((pEncoding == null) && ("RE".equalsIgnoreCase(propertyName.trim().toUpperCase()))) {
                        byte[] bytes = propertyValue.getBytes();
                        if ((bytes.length > 2 && bytes[0] == -70 && bytes[1] == -38) ||
                                (bytes.length > 2 && bytes[0] == -80 && bytes[1] == -41)) {
                            return readSGFNodeTree(pFile, "GBK");
                        }
                    }

                    propertyValue = "";
                    readingPropertyValue = false;
                    readingPropertyName = true;
                    readingFirstPropertyNameChar = true;
                } else if (readingPropertyValue) {
                    propertyValue += character;
                } else if (readingPropertyName) {
                    if (firstPropertyInVariation)
                    {
                        // same code as when an ";" is encountered.
                        currentNode = new SGFNode(currentNode);
                        if (currentNode.getParentNode() != null) currentNode.getParentNode().addNextNode(currentNode);
                        firstPropertyInVariation = false;
                        readingPropertyName = true;
                        readingFirstPropertyNameChar = true;
                        prevPropertyName = null;
                    }

                    if (readingFirstPropertyNameChar) {
                        readingFirstPropertyNameChar = false;
                        prevPropertyName = propertyName;
                        propertyName = "";
                    }
                    if (character >= ' ') propertyName += character;
                } else if (character == '\n') {
                    line++;
                    col = 0;
                } else if (character == '\r') {
                } else if (isInError) {
                    // there's an error, but already in error state ...
                    // there's no need to mention the error again.
                    continue;
                } else {
                    // found error, store it in the root node
                    isInError = true;
                    fileNode.addWarning("File Structure Error - Unexpected character [" + character + "] at [" + line + ":" + col + "]");
                    continue;
                }

                // managed to perform this loop without errors.
                isInError = false;
            }

            return fileNode;
        } catch (Exception e) {
            SGFFileNode node = new SGFFileNode(pFile.getName());
            node.addWarning("Unexpected Fatal Exception");
            e.printStackTrace();
            return node;
        }
    }

    private String fixFileFormatIssue(String fileContent) {
        // common issue: files that don't start with "(;..." but start with "(...".
        Pattern pattern = Pattern.compile("^(\\s*\\(\\s*)[A-Z]+.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fileContent);
        if (matcher.matches()) {
            String start = matcher.group(1);
            fileContent = "(;" + fileContent.substring(start.length());
        }

        fileContent = fileContent.replaceAll("\u6bb5\\)", "\u6bb5");
        fileContent = fileContent.replaceAll("^FF\\[1]\\(;GM", "(;GM");

        return fileContent;
    }

    private List<Intersection> getPointList(String coordinates, int boardSize) {
        if ((coordinates == null) || ("".equalsIgnoreCase(coordinates.trim()))) return null;

        List<Intersection> pointList = new ArrayList<>(1);
        char[] array = coordinates.toCharArray();

        Integer x = null;
        Intersection intersection = null;
        for (int i = 0, iLength = array.length; i < iLength; i++) {
            char cursor = array[i];
            Character nearFuture = (i + 1 < iLength) ? Character.valueOf(array[i + 1]) : null;
            if ((cursor == ',') || ((intersection != null) && (cursor == ':'))) {
                // will be processed after reading the next point
            } else if (x == null) {
                x = (int) cursor - 'a';
            } else {
                Integer y = (int) cursor - 'a';
                if (intersection == null) {
                    // add p1 or store it for a ':' (range of points)
                    intersection = Intersection.valueOf(x, y);
                    if ((nearFuture == null) || (':' != (nearFuture.charValue()))) {
                        pointList.add(intersection);
                        intersection = null;
                    }
                } else {
                    // add all points
                    for (int m = intersection.getX(); m <= x; m++)
                        for (int n = intersection.getY(); n <= y; n++)
                            pointList.add(Intersection.valueOf(m, n));
                }

                // clear points
                x = null;
            }
        }


        if (pointList.size() == 1) {
            Intersection intx = pointList.get(0);
            if ((intx.getX() == boardSize) && (intx.getY() == boardSize))
            {
              // a pass move
              return null;
            }
        }

        return pointList;
    }
}
