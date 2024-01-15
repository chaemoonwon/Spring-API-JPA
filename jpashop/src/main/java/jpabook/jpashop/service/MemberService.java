package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.MemberRepositoryOld;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
//@AllArgsConstructor
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

//    @Autowired //생성자가 한 개만 있을 경우 생략가능
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //수정자 주입
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /*
     * 회원 조회
     * */
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if(!(findMembers.isEmpty())) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    @Transactional(readOnly = true)
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //회원 한명 조회
    @Transactional(readOnly = true)
    public Member findOne(Long memberid) {
        return memberRepository.findById(memberid).get();
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id).get();
        member.setName(name);
    }
}
