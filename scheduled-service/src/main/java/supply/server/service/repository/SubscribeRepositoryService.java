package supply.server.service.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import supply.server.config.exception.DbException;
import supply.server.data.RpSubscribe;
import supply.server.data.Subscribe;

import java.util.List;

@AllArgsConstructor
public class SubscribeRepositoryService {

    private final RpSubscribe rpSubscribe;

    public List<Subscribe> getAll() {
        List<Subscribe> subscribes;
        try {
            subscribes = rpSubscribe.getAll();
        } catch (Exception e) {
            throw new DbException("Error while getting all subscribes");
        }
        return subscribes;
    }

}
