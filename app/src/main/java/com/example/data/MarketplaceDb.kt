package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ==========================================
// ROOM ENTITIES
// ==========================================

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val civilId: String = "",
    val drivingLicenceNo: String = "",
    val licenceExpiry: String = "",
    val nationality: String = "",
    val dateOfBirth: String = "",
    val role: String = "CUSTOMER", // "CUSTOMER", "BRANCH", "INSURER", "EMPLOYEE", "ADMIN"
    val branchId: String? = null,
    val insurerId: String? = null
)

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey val id: String, // Vehicle Registration/Plate Number
    val customerId: String,
    val ownerName: String,
    val make: String,
    val model: String,
    val year: Int,
    val chassisNumber: String,
    val engineNumber: String,
    val currentInsurer: String = "",
    val hasNcb: Boolean = true,
    val previousClaims: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "leads")
data class Lead(
    @PrimaryKey val id: String, // Lead ID like "LA-MCT-1029"
    val customerId: String,
    val customerName: String,
    val vehicleNumber: String,
    val vehicleMake: String,
    val vehicleModel: String,
    val vehicleYear: Int,
    val chassisNumber: String = "",
    val engineNumber: String = "",
    val status: String = "PENDING_ACCEPTED", // "PENDING_ACCEPTED", "COMPETING", "ACCEPTED_QUOTE", "POLICY_ISSUED", "REJECTED_ALL", "REOPENED", "ASSIGNED_AFFINITE"
    val acceptedByInsurerIds: String = "", // Comma-separated insurer IDs (max 3)
    val timestamp: Long = System.currentTimeMillis(),
    val ncbPercentage: Int = 0,
    val previousClaims: Boolean = false,
    val driverAge: Int = 30,
    val drivingExperienceYears: Int = 5,
    val coverageType: String = "Comprehensive", // "Comprehensive", "Third Party"
    val agencyRepair: Boolean = true,
    val preferredBranchId: String = "MCT-01",
    val renewalDate: Long = System.currentTimeMillis() + 31536000000L, // 1 year later
    val duplicateLead: Boolean = false,
    val assignedEmployeeId: String = ""
)

@Entity(tableName = "quotations")
data class Quotation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val leadId: String,
    val insurerId: String,
    val insurerName: String,
    val premium: Double,
    val excess: Double,
    val benefits: String, // Comma separated benefits
    val exclusions: String, // Comma separated exclusions
    val addons: String, // Comma separated addons
    val validityDays: Int = 15,
    val rating: Float = 4.5f,
    val status: String = "PENDING", // "PENDING", "ACCEPTED", "REJECTED"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val leadId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "CUSTOMER", "BRANCH", "INSURER", "EMPLOYEE"
    val messageText: String,
    val attachmentUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey val id: String,
    val name: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String,
    val email: String
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val targetRole: String? = null, // "CUSTOMER", "BRANCH", "INSURER", "EMPLOYEE", "ADMIN"
    val targetUserId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

// ==========================================
// ROOM DAOS
// ==========================================

@Dao
interface MarketplaceDao {
    // Profiles
    @Query("SELECT * FROM user_profiles")
    fun getAllUserProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE id = :id")
    suspend fun getUserProfile(id: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // Vehicles
    @Query("SELECT * FROM vehicles WHERE customerId = :customerId")
    fun getVehiclesForCustomer(customerId: String): Flow<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle)

    // Leads
    @Query("SELECT * FROM leads ORDER BY timestamp DESC")
    fun getAllLeadsFlow(): Flow<List<Lead>>

    @Query("SELECT * FROM leads WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getLeadsForCustomer(customerId: String): Flow<List<Lead>>

    @Query("SELECT * FROM leads WHERE id = :id")
    suspend fun getLeadById(id: String): Lead?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: Lead)

    @Update
    suspend fun updateLead(lead: Lead)

    // Quotations
    @Query("SELECT * FROM quotations WHERE leadId = :leadId ORDER BY premium ASC")
    fun getQuotationsForLead(leadId: String): Flow<List<Quotation>>

    @Query("SELECT * FROM quotations WHERE id = :id")
    suspend fun getQuotationById(id: Int): Quotation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotation(quotation: Quotation)

    @Update
    suspend fun updateQuotation(quotation: Quotation)

    // Chat
    @Query("SELECT * FROM chat_messages WHERE leadId = :leadId ORDER BY timestamp ASC")
    fun getChatMessagesFlow(leadId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    // Branches
    @Query("SELECT * FROM branches")
    fun getAllBranchesFlow(): Flow<List<Branch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int)
}

// ==========================================
// ROOM DATABASE
// ==========================================

@Database(
    entities = [
        UserProfile::class,
        Vehicle::class,
        Lead::class,
        Quotation::class,
        ChatMessage::class,
        Branch::class,
        Notification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): MarketplaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "affinite_marketplace_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ==========================================
// REPOSITORY
// ==========================================

class MarketplaceRepository(private val dao: MarketplaceDao) {
    val allUserProfiles: Flow<List<UserProfile>> = dao.getAllUserProfiles()
    val allLeads: Flow<List<Lead>> = dao.getAllLeadsFlow()
    val allBranches: Flow<List<Branch>> = dao.getAllBranchesFlow()
    val allNotifications: Flow<List<Notification>> = dao.getAllNotificationsFlow()

    suspend fun getUserProfile(id: String): UserProfile? = dao.getUserProfile(id)
    suspend fun insertUserProfile(profile: UserProfile) = dao.insertUserProfile(profile)

    fun getVehiclesForCustomer(customerId: String): Flow<List<Vehicle>> = dao.getVehiclesForCustomer(customerId)
    suspend fun insertVehicle(vehicle: Vehicle) = dao.insertVehicle(vehicle)

    fun getLeadsForCustomer(customerId: String): Flow<List<Lead>> = dao.getLeadsForCustomer(customerId)
    suspend fun getLeadById(id: String): Lead? = dao.getLeadById(id)
    suspend fun insertLead(lead: Lead) = dao.insertLead(lead)
    suspend fun updateLead(lead: Lead) = dao.updateLead(lead)

    fun getQuotationsForLead(leadId: String): Flow<List<Quotation>> = dao.getQuotationsForLead(leadId)
    suspend fun getQuotationById(id: Int): Quotation? = dao.getQuotationById(id)
    suspend fun insertQuotation(quotation: Quotation) = dao.insertQuotation(quotation)
    suspend fun updateQuotation(quotation: Quotation) = dao.updateQuotation(quotation)

    fun getChatMessagesFlow(leadId: String): Flow<List<ChatMessage>> = dao.getChatMessagesFlow(leadId)
    suspend fun insertChatMessage(message: ChatMessage) = dao.insertChatMessage(message)

    suspend fun insertBranch(branch: Branch) = dao.insertBranch(branch)
    suspend fun insertNotification(notification: Notification) = dao.insertNotification(notification)
    suspend fun markNotificationAsRead(id: Int) = dao.markNotificationAsRead(id)
}
