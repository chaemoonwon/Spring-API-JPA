package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepositoryOld;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
/* Test에서 Transactional이 기본적으로 Rollback을 하기 때문에 DB에 insert가 되지 않음
* 그러므로 Rollback을 false해주면 insert가 되는 것을 확인할 수 잇음
* */
class MemberServiceTest {


    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepositoryOld memberRepositoryOld;
    @Autowired
    private EntityManager em;



    //회원 가입
    @Test
//    @Rollback(value = false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("chae");

        //when
        Long saveId = memberService.join(member);

        //then
        em.flush();
        assertEquals(member, memberRepositoryOld.findOne(saveId));
    }


    //중복 회원 예외
    @Test
    public void 중복회원예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("chae");
        Member member2 = new Member();
        member2.setName("chae");


        //when
        memberService.join(member1);

//        try{
//            memberService.join(member2);    //중복 회원 조회
//        } catch (IllegalStateException e) {
//            return;
//        }
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));

        //then
//        fail("예외가 발생해야 한다");


    }
}