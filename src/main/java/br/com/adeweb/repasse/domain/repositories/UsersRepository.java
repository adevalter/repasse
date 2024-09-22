package br.com.adeweb.repasse.domain.repositories;

import br.com.adeweb.repasse.domain.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
