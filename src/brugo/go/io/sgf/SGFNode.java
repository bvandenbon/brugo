package brugo.go.io.sgf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SGFNode {
    private Map<String, String> attributeMap;
    private SGFNode parentNode;
    private List<SGFNode> nextNodeList;

    public SGFNode(SGFNode parentNode) {
        this.parentNode = parentNode;
    }

    public void addNextNode(SGFNode pNextNode) {
        if (nextNodeList == null) nextNodeList = new ArrayList<>();
        nextNodeList.add(pNextNode);
    }

    public List<SGFNode> getNextNodeList() {
        return nextNodeList;
    }

    public SGFNode getParentNode() {
        return parentNode;
    }

    public String getProperty(String pPropertyName) {
        if (attributeMap == null) return null;
        return attributeMap.get(pPropertyName);
    }

    public String removeProperty(String pPropertyName) {
        if (attributeMap == null) return null;
        return attributeMap.remove(pPropertyName);
    }

    public Map<String, String> getMap() {
        return attributeMap;
    }

    public void putProperty(String propertyName, String propertyValue) {
        if (propertyName == null) return;
        if (attributeMap == null) attributeMap = new HashMap<>();

        // if the value is already assigned, merge it the 2nd time
        String currentValue = attributeMap.get(propertyName);
        if (currentValue != null) propertyValue = currentValue + "," + propertyValue;

        attributeMap.put(propertyName.toUpperCase(), propertyValue);
    }

    public Double getDoubleProperty(SGFFileNode fileNode, String property, Double defaultValue, boolean remove) {
        String propertyValue = getProperty(property);
        if (propertyValue == null) return defaultValue;
        if (remove) removeProperty(property);
        if ("".equals(property.trim())) return defaultValue;
        double value;
        try {
            propertyValue = propertyValue.replaceAll(",", ".");
            value = Double.parseDouble(propertyValue);
        } catch (NumberFormatException e) {
            fileNode.addWarning("Invalid value in " + property + " property. Will use default '" + defaultValue + "' (found: '" + propertyValue + "').");
            return defaultValue;
        }
        return value;
    }

    public Integer getIntegerProperty(SGFFileNode fileNode, String property, Integer defaultValue, boolean remove) {
        String propertyValue = getProperty(property);
        if (propertyValue == null) return defaultValue;
        if (remove) removeProperty(property);
        if ("".equals(property.trim())) return defaultValue;
        int value;
        try {
            value = Integer.parseInt(propertyValue);
        } catch (NumberFormatException e) {
            fileNode.addWarning("Invalid value in " + property + " property. Will use default '" + defaultValue + "' (found: '" + propertyValue + "').");
            return defaultValue;
        }
        return value;
    }

    public String getStringProperty(SGFFileNode fileNode, String property, String defaultValue, boolean remove) {
        String propertyValue = getProperty(property);
        if (propertyValue == null) return defaultValue;
        if (remove) removeProperty(property);
        if ("".equals(property.trim())) return defaultValue;
        return propertyValue;
    }
}
