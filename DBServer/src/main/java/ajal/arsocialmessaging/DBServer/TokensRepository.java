package ajal.arsocialmessaging.DBServer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokensRepository extends CrudRepository<Token, Integer> {
}
