package com.sparta.areadevelopment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)

public abstract class Timestamped {

    // 해당 추상 클래스를 상속받는 클래스에서는 모두 creatAt, modifiedAt
    // 컬럼이 생성됩니다.
    @CreatedDate
    // 해당 updatable option은 생성 초기에만 시간을 저장하고 변경이 되었을 때 시간을 바꾸지 않기 위해서 입니다.
    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;


    // 마지막 변경 시간을 저장합니다.
    @LastModifiedDate
    @Column(nullable = false)
    // 자바의 데이트 타입을 매핑할 때 사용합니다.-> Date, Calendar, TIMESTAMP type 이 있습니다.
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deletedAt;

    // service 에서 탈퇴를 할 때 해당 메서드를 이용하여 값을 넣습니다.
    protected void setDeletedAt(LocalDateTime now) {
        this.deletedAt = now;
    }

    //
    public boolean isDeleted() {
        if (this.deletedAt != null) {
            return true;
        }
        return false;
    }
}