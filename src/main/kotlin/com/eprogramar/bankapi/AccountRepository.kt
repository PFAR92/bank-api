package com.eprogramar.bankapi;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface AccountRepository : JpaRepository<Account, Long> {


    @Transactional
    @Modifying
    @Query("delete from Account a where a.document = ?1")
    fun deleteByDocument(document: String)

    @Query("select a from Account a where a.document = ?1")
    fun findByDocument(document: String)
}