package supply.server.requestEntity.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import supply.server.data.utils.user.UserPermission;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserPermissionDeserializer extends JsonDeserializer<List<UserPermission>> {

    @Override
    public List<UserPermission> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<String> permissions = p.readValueAs(new TypeReference<List<String>>() {}); // Парсим JSON-массив строк
        return permissions.stream()
                .map(UserPermission::valueOf)  // Преобразуем каждую строку в UserPermission
                .collect(Collectors.toList());
    }
}
