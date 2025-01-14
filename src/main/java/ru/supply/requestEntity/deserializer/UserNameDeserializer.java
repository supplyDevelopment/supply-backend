package ru.supply.requestEntity.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ru.supply.configuration.exception.IncorrectInputException;
import ru.supply.data.utils.UserName;

import java.io.IOException;

public class UserNameDeserializer extends JsonDeserializer<UserName> {
    @Override
    public UserName deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (node.has("firstName")
                && node.has("secondName")) {
            return new UserName(
                    node.get("firstName").asText(),
                    node.get("secondName").asText(),
                    node.has("lastName") ? node.get("lastName").asText() : null
            );
        }
        throw new IncorrectInputException("User name is invalid");
    }
}
