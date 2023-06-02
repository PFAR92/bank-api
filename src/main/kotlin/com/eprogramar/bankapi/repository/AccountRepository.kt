package com.eprogramar.bankapi.repository;

import com.eprogramar.bankapi.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long> {

}