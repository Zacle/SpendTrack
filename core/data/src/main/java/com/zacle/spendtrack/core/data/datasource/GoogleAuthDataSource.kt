package com.zacle.spendtrack.core.data.datasource

import android.content.Context
import com.zacle.spendtrack.core.model.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface GoogleAuthDataSource {
    fun authenticateWithGoogle(context: Context): Flow<AuthResult?>
}