package ajal.arsocialmessaging.DBServer;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface MessagesRepository extends CrudRepository<Message, Integer>{
}
