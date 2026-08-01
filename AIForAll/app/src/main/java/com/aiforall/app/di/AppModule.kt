package com.aiforall.app.di

import com.aiforall.app.data.repository.PostRepositoryImpl
import com.aiforall.app.domain.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides Firebase SDK singletons. Kept separate from the @Binds module
 * below because @Provides is needed for classes we don't own (can't add
 * @Inject constructors to FirebaseFirestore itself).
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides @Singleton
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}

/**
 * Binds interfaces to implementations. Using @Binds (not @Provides) here
 * since PostRepositoryImpl already has an @Inject constructor — this is
 * just wiring, no manual construction needed.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository
}
