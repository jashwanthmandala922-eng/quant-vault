package com.quantvault.app.data.vault.datasource.disk.dao

import com.quantvault.core.data.repository.util.bufferedMutableSharedFlow
import com.quantvault.app.data.vault.datasource.disk.entity.DomainsEntity
import kotlinx.coroutines.flow.Flow

class FakeDomainsDao : DomainsDao {
    var storedDomains: DomainsEntity? = null

    var deleteDomainsCalled: Boolean = false
    var getDomainsCalled: Boolean = false
    var insertDomainsCalled: Boolean = false

    private val mutableDomainsFlow = bufferedMutableSharedFlow<DomainsEntity?>()

    override suspend fun deleteDomains(userId: String) {
        deleteDomainsCalled = true
    }

    override fun getDomainsFlow(userId: String): Flow<DomainsEntity?> {
        getDomainsCalled = true
        return mutableDomainsFlow
    }

    override suspend fun insertDomains(domains: DomainsEntity) {
        insertDomainsCalled = true
        storedDomains = domains
        mutableDomainsFlow.tryEmit(domains)
    }
}




