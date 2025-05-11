package com.web.dto;

import java.time.LocalDateTime;

import com.web.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DtoAdminAssignment {

	private Long id;

    private Long assignedByUserId;  // Rolü değiştiren Super Admin'in ID'si

    private Long assignedUserId;  // Rolü değiştirilen kullanıcı ID'si

    private Role newRole;  // Atanan yeni rol

    private LocalDateTime assignedAt;  // Rol atama zamanı
}
