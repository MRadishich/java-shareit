package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDto itemDto, long userId) {
        return post("", itemDto, userId);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, long itemId, long userId) {
        return post("/" + itemId + "/comment", commentDto, userId);
    }

    public ResponseEntity<Object> getItemsByOwnerId(long userId, int from, int size, String sort, String dir) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size,
                "sort", sort,
                "dir", dir
        );

        return get("?from={from}&size={size}&sort={sort}&dir={dir}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByText(String text, int from, int size, String sort, String dir) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size,
                "sort", sort,
                "dir", dir
        );

        return get("/search?text={text}&from={from}&size={size}&sort={sort}&dir={dir}", null, parameters);
    }

    public ResponseEntity<Object> updateItemById(long itemId, ItemDto itemDto, long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public void deleteItemById(long itemId, long userId) {
        delete("/" + itemId, userId);
    }
}
