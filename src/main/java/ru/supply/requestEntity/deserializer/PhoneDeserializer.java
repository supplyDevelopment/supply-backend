package ru.supply.requestEntity.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.supply.data.utils.Phone;

import java.io.IOException;

public class PhoneDeserializer extends JsonDeserializer<Phone> {
    @Override
    public Phone deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return new Phone(jsonParser.getValueAsString());
    }
}
