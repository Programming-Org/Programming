package io.github.org.programming.backendv1.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class JsonUtil {
    public static ArrayNode optionsToJson(List<OptionData> optionData) {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (OptionData option : optionData) {
           arrayNode.add(JsonNodeFactory.instance.objectNode()
                   .put("name", option.getName())
                   .put("description", option.getDescription())
                   .put("type", option.getType().name())
                   .put("required", option.isRequired()));
        }
        return arrayNode;
    }
}
