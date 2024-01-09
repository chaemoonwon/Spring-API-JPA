package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceContext
    private final EntityManager em;



//    public MemberRepository(EntityManager em) {
//        this.em = em;
//    }

    //    @PersistenceUnit
//    private EntityManagerFactory em;

    public void save(Member member) {
        em.persist(member);
    };

    // 회원 단 건 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    //JPQL과 SQL의 차이
    //JPQL은 Entity객체를 대상으로 query를 하는 반면,
    //SQL은 Table을 대상으로 query를 함.
    // 모든 회원 조회 => JPQL을 이용해 조회
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    // 회원 이름을 통해 조회
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
