package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    protected ResponseEntity<Object> createRequest(ItemRequestInputDto itemRequestInputDto, long userId) {
        return post("", itemRequestInputDto, userId);
    }

    public ResponseEntity<Object> getRequestsByRequesterId(long userId, String sort, String dir) {
        Map<String, Object> parameters = Map.of(
                "sort", sort,
                "dir", dir
        );

        return get("?sort={sort}&dir={dir}", userId, parameters);
    }

    public ResponseEntity<Object> getOtherRequests(long userId, int from, int size, String sort, String dir) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size,
                "sort", sort,
                "dir", dir
        );

        return get("/all?from={from}&size={size}&sort={sort}&dir={dir}", userId, parameters);
    }

    public ResponseEntity<Object> getRequestById(long requestId, long userId) {
        return get("/" + requestId, userId);
    }
}
