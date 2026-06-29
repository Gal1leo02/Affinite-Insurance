package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repo = MarketplaceRepository(db.dao())

    // Language & Theme
    private val _isArabic = MutableStateFlow(false)
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Portal & Screen Navigation
    private val _currentPortal = MutableStateFlow("CUSTOMER") // CUSTOMER, BRANCH, INSURER, EMPLOYEE, ADMIN
    val currentPortal: StateFlow<String> = _currentPortal.asStateFlow()

    private val _activeScreen = MutableStateFlow("customer_home")
    val activeScreen: StateFlow<String> = _activeScreen.asStateFlow()

    // Active Objects
    private val _currentLeadId = MutableStateFlow<String?>(null)
    val currentLeadId: StateFlow<String?> = _currentLeadId.asStateFlow()

    private val _activeDepartment = MutableStateFlow("SALES") // SALES, OPERATIONS, ACCOUNTS, SUPPORT, RENEWALS, HR, MANAGEMENT
    val activeDepartment: StateFlow<String> = _activeDepartment.asStateFlow()

    // OCR / Upload State
    private val _ocrProgress = MutableStateFlow(false)
    val ocrProgress: StateFlow<Boolean> = _ocrProgress.asStateFlow()

    private val _extractedOcrData = MutableStateFlow<Map<String, String>?>(null)
    val extractedOcrData: StateFlow<Map<String, String>?> = _extractedOcrData.asStateFlow()

    private val _uploadedDocumentType = MutableStateFlow<String?>(null) // "MULKIYA", "LICENCE", "CIVIL_ID"
    val uploadedDocumentType: StateFlow<String?> = _uploadedDocumentType.asStateFlow()

    // AI Assistant state
    private val _aiAssistantResponse = MutableStateFlow<String?>(null)
    val aiAssistantResponse: StateFlow<String?> = _aiAssistantResponse.asStateFlow()

    private val _aiAssistantLoading = MutableStateFlow(false)
    val aiAssistantLoading: StateFlow<Boolean> = _aiAssistantLoading.asStateFlow()

    private val _aiChatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(listOf(
        "Marhaban! I am your Affinite AI Assistant. I can help extract details from your documents, compare quotes, and explain insurance concepts. How can I assist you today?" to false
    ))
    val aiChatMessages: StateFlow<List<Pair<String, Boolean>>> = _aiChatMessages.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Map selection
    private val _selectedMapBranch = MutableStateFlow<Branch?>(null)
    val selectedMapBranch: StateFlow<Branch?> = _selectedMapBranch.asStateFlow()

    // Selected items
    private val _selectedComplaintText = MutableStateFlow<String?>(null)
    val selectedComplaintText: StateFlow<String?> = _selectedComplaintText.asStateFlow()

    // Main Flows
    val leads: StateFlow<List<Lead>> = repo.allLeads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val branches: StateFlow<List<Branch>> = repo.allBranches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<Notification>> = repo.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered / Computed Flows
    val currentCustomerLeads: Flow<List<Lead>> = leads.map { list ->
        list.filter { it.customerId == "CUST-001" }
    }

    val currentLeadQuotations: Flow<List<Quotation>> = _currentLeadId.flatMapLatest { leadId ->
        if (leadId != null) repo.getQuotationsForLead(leadId) else flowOf(emptyList())
    }

    val currentLeadMessages: Flow<List<ChatMessage>> = _currentLeadId.flatMapLatest { leadId ->
        if (leadId != null) repo.getChatMessagesFlow(leadId) else flowOf(emptyList())
    }

    // Mock Login Contexts
    val currentCustomerProfile = MutableStateFlow(
        UserProfile("CUST-001", "Ahmed bin Salim Al-Kharusi", "+968 9123 4567", "ahmed.alkharusi@gmail.com", "109382104", "D-8392102", "2031-10-12", "Omani", "1989-05-14", "CUSTOMER")
    )
    val currentBranchProfile = UserProfile("BR-01", "Salim Al-Harthy", "+968 9988 7766", "salim.harthy@affinite.com", "11223344", "D-11223344", "2030-01-01", "Omani", "1985-06-15", "BRANCH", "MCT-01")
    
    val currentInsurers = listOf(
        UserProfile("INS-01", "NLG Bider", "+968 9200 1100", "bids@nlg.om", "22334455", "", "", "Omani", "", "INSURER", null, "NLG"),
        UserProfile("INS-02", "Oman Insurance Bider", "+968 9200 1122", "bids@oic.om", "22334466", "", "", "Omani", "", "INSURER", null, "OIC"),
        UserProfile("INS-03", "Muscat Insurer", "+968 9200 1133", "bids@muscat.om", "22334477", "", "", "Omani", "", "INSURER", null, "MIC"),
        UserProfile("INS-04", "Dhofar Insurer", "+968 9200 1144", "bids@dhofar.om", "22334488", "", "", "Omani", "", "INSURER", null, "DIC")
    )
    val activeInsurerIndex = MutableStateFlow(0)
    val currentInsurerProfile: Flow<UserProfile> = activeInsurerIndex.map { index -> currentInsurers[index] }

    init {
        // Seed default initial data
        viewModelScope.launch(Dispatchers.IO) {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() {
        // Insert Customer Profile
        repo.insertUserProfile(currentCustomerProfile.value)

        // Insert Branches
        val branchesList = listOf(
            Branch("MCT-01", "Muscat Head Office", "Al Khuwair, Muscat", 23.5937, 58.4234, "+968 2470 1111", "muscat@affinite.com"),
            Branch("SLL-02", "Salalah Branch", "23rd July Street, Salalah", 17.0151, 54.0924, "+968 2320 2222", "salalah@affinite.com"),
            Branch("SOH-03", "Sohar Branch", "Al Hamber, Sohar", 24.3486, 56.7156, "+968 2684 3333", "sohar@affinite.com"),
            Branch("NIZ-04", "Nizwa Branch", "Nizwa Souq, Nizwa", 22.9333, 57.5333, "+968 2541 4444", "nizwa@affinite.com"),
            Branch("SEE-05", "Seeb Branch", "Maabila, Seeb", 23.6700, 58.1800, "+968 2445 5555", "seeb@affinite.com")
        )
        branchesList.forEach { repo.insertBranch(it) }

        // Set Default Selection on Map
        _selectedMapBranch.value = branchesList[0]

        // Check if leads exist; if not, seed a couple of interesting leads
        val existing = leads.value
        if (existing.isEmpty()) {
            val sampleLead1 = Lead(
                id = "LA-2026-0681",
                customerId = "CUST-001",
                customerName = "Ahmed bin Salim Al-Kharusi",
                vehicleNumber = "8293 DD",
                vehicleMake = "Lexus",
                vehicleModel = "LX570",
                vehicleYear = 2021,
                chassisNumber = "JTHHY1029D8392101",
                engineNumber = "EG-LX-570-39",
                status = "COMPETING",
                acceptedByInsurerIds = "OIC,MIC",
                ncbPercentage = 30,
                previousClaims = false,
                driverAge = 37,
                drivingExperienceYears = 14,
                coverageType = "Comprehensive",
                agencyRepair = true,
                preferredBranchId = "MCT-01",
                timestamp = System.currentTimeMillis() - 7200000 // 2 hours ago
            )

            val sampleLead2 = Lead(
                id = "LA-2026-0692",
                customerId = "CUST-002",
                customerName = "Muna Al-Siyabi",
                vehicleNumber = "1092 AR",
                vehicleMake = "Mazda",
                vehicleModel = "CX-5",
                vehicleYear = 2022,
                chassisNumber = "JM1KE1029C8290192",
                engineNumber = "EG-MZ-829102",
                status = "PENDING_ACCEPTED",
                acceptedByInsurerIds = "",
                ncbPercentage = 10,
                previousClaims = true,
                driverAge = 28,
                drivingExperienceYears = 4,
                coverageType = "Third Party",
                agencyRepair = false,
                preferredBranchId = "SEE-05",
                timestamp = System.currentTimeMillis() - 1000000
            )

            val sampleLead3 = Lead(
                id = "LA-2026-0512",
                customerId = "CUST-001",
                customerName = "Ahmed bin Salim Al-Kharusi",
                vehicleNumber = "9119 KA",
                vehicleMake = "Toyota",
                vehicleModel = "Avalon",
                vehicleYear = 2018,
                chassisNumber = "4T1BF11299C102931",
                engineNumber = "EG-TY-10293",
                status = "POLICY_ISSUED",
                acceptedByInsurerIds = "OIC,MIC,NLG",
                ncbPercentage = 40,
                previousClaims = false,
                driverAge = 37,
                drivingExperienceYears = 14,
                coverageType = "Comprehensive",
                agencyRepair = false,
                preferredBranchId = "MCT-01",
                timestamp = System.currentTimeMillis() - 30 * 24 * 3600000L // 30 days ago
            )

            repo.insertLead(sampleLead1)
            repo.insertLead(sampleLead2)
            repo.insertLead(sampleLead3)

            // Seed quotations for Lead 1 (Lexus LX570)
            val quote1 = Quotation(
                leadId = "LA-2026-0681",
                insurerId = "OIC",
                insurerName = "Oman Insurance Company (OIC)",
                premium = 340.0,
                excess = 100.0,
                benefits = "Oman & UAE Road Coverage, AAA Roadside Assistance, Personal Accident for Passengers",
                exclusions = "Off-road desert dune coverage excluded",
                addons = "Windscreen protection (+OMR 15), Rent-a-car service (+OMR 25)",
                validityDays = 15,
                rating = 4.8f,
                status = "PENDING"
            )
            val quote2 = Quotation(
                leadId = "LA-2026-0681",
                insurerId = "MIC",
                insurerName = "Muscat Insurance Company",
                premium = 375.0,
                excess = 50.0,
                benefits = "Agency Repair, 24/7 Roadside Assistance, Free Valet service",
                exclusions = "Mechanical breakdowns",
                addons = "Zero Depreciation cover (+OMR 30)",
                validityDays = 10,
                rating = 4.4f,
                status = "PENDING"
            )
            repo.insertQuotation(quote1)
            repo.insertQuotation(quote2)

            // Seed Chat Messages for Lead 1 Sales Room
            repo.insertChatMessage(ChatMessage(leadId = "LA-2026-0681", senderId = "CUST-001", senderName = "Ahmed Al-Kharusi", senderRole = "CUSTOMER", messageText = "Hi, can we get agency repair included with the Oman Insurance quote?", timestamp = System.currentTimeMillis() - 3600000))
            repo.insertChatMessage(ChatMessage(leadId = "LA-2026-0681", senderId = "MCT-01", senderName = "Salim Al-Harthy", senderRole = "BRANCH", messageText = "Hello Ahmed, let me check with Oman Insurance if they can revise the quote to include agency repair.", timestamp = System.currentTimeMillis() - 1800000))

            // Seed some active notifications
            repo.insertNotification(Notification(title = "Welcome to Affinite", body = "Your enterprise insurance marketplace is active. Register, upload documents or start a quote now!", targetRole = "CUSTOMER"))
            repo.insertNotification(Notification(title = "New Leads Available", body = "Lead LA-2026-0692 submitted in Seeb. Head over to the Insurance Marketplace to claim!", targetRole = "INSURER"))
        }
    }

    // ==========================================
    // PORTAL OPERATIONS
    // ==========================================

    fun toggleLanguage() {
        _isArabic.value = !_isArabic.value
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun switchPortal(portal: String) {
        _currentPortal.value = portal
        _activeScreen.value = when (portal) {
            "CUSTOMER" -> "customer_home"
            "BRANCH" -> "branch_home"
            "INSURER" -> "insurer_home"
            "EMPLOYEE" -> "employee_home"
            "ADMIN" -> "admin_home"
            else -> "customer_home"
        }
    }

    fun setScreen(screen: String) {
        _activeScreen.value = screen
    }

    fun selectLead(leadId: String?) {
        _currentLeadId.value = leadId
    }

    fun selectMapBranch(branch: Branch) {
        _selectedMapBranch.value = branch
    }

    fun setDepartment(department: String) {
        _activeDepartment.value = department
    }

    fun setInsurerIndex(index: Int) {
        viewModelScope.launch {
            activeInsurerIndex.value = index
        }
    }

    // ==========================================
    // CUSTOMER ACTIONS
    // ==========================================

    fun onRegisterCustomer(fullName: String, phone: String, email: String, civilId: String, licNo: String, expiry: String, nat: String, dob: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = UserProfile("CUST-001", fullName, phone, email, civilId, licNo, expiry, nat, dob, "CUSTOMER")
            currentCustomerProfile.value = updated
            repo.insertUserProfile(updated)
            repo.insertNotification(Notification(
                title = "Profile Completed",
                body = "Your profile has been successfully updated with Omani Civil ID and Driver details.",
                targetRole = "CUSTOMER",
                targetUserId = "CUST-001"
            ))
        }
    }

    // OCR Document Upload Simulation
    fun onUploadDocument(docType: String, base64Image: String) {
        _ocrProgress.value = true
        _uploadedDocumentType.value = docType
        _extractedOcrData.value = null

        viewModelScope.launch(Dispatchers.IO) {
            // Trigger actual Gemini API call
            val prompt = """
                Perform an OCR on this uploaded $docType document for Oman Insurance Market. 
                Extract: Vehicle Number, Chassis Number, Engine Number, Owner Name, Vehicle Make, Vehicle Model, Vehicle Year, Driver Name, Licence Number, Licence Expiry, Nationality, Date of Birth.
                Respond strictly in JSON format.
            """.trimIndent()

            val aiResult = GeminiApiClient.generateAiContent(prompt, base64Image = base64Image, imageMimeType = "image/jpeg")
            
            // Wait slightly for smooth visual progression
            delay(2000)

            // Try to parse JSON extracted fields
            val extractedMap = parseOcrJson(aiResult)
            _extractedOcrData.value = extractedMap
            _ocrProgress.value = false

            repo.insertNotification(Notification(
                title = "AI OCR Processing Complete",
                body = "Successfully extracted details from your uploaded $docType using Gemini AI.",
                targetRole = "CUSTOMER",
                targetUserId = "CUST-001"
            ))
        }
    }

    private fun parseOcrJson(jsonStr: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        try {
            // Standard simple manual parser to avoid JSON library parsing exceptions on unexpected formatting
            val clean = jsonStr.replace("{", "").replace("}", "").replace("\"", "")
            val lines = clean.split(",")
            for (line in lines) {
                val parts = line.split(":")
                if (parts.size >= 2) {
                    val key = parts[0].trim()
                    val value = parts.drop(1).joinToString(":").trim()
                    result[key] = value
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fill with baseline Omani details if anything is missing
        if (result.isEmpty()) {
            result["vehicleNumber"] = "9812 AR"
            result["ownerName"] = "Ahmed bin Salim Al-Kharusi"
            result["vehicleMake"] = "Toyota"
            result["vehicleModel"] = "Land Cruiser"
            result["vehicleYear"] = "2023"
            result["chassisNumber"] = "MJDH3901SND93012"
            result["engineNumber"] = "EG-O83912"
        }
        return result
    }

    fun onConfirmOcrDetails(confirmedData: Map<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val make = confirmedData["vehicleMake"] ?: "Toyota"
            val model = confirmedData["vehicleModel"] ?: "Land Cruiser"
            val year = confirmedData["vehicleYear"]?.toIntOrNull() ?: 2023
            val plate = confirmedData["vehicleNumber"] ?: "9812 AR"
            val owner = confirmedData["ownerName"] ?: "Ahmed bin Salim Al-Kharusi"
            val chassis = confirmedData["chassisNumber"] ?: "MJDH3901SND93012"
            val engine = confirmedData["engineNumber"] ?: "EG-O83912"

            val vehicle = Vehicle(
                id = plate,
                customerId = "CUST-001",
                ownerName = owner,
                make = make,
                model = model,
                year = year,
                chassisNumber = chassis,
                engineNumber = engine
            )
            repo.insertVehicle(vehicle)
            _extractedOcrData.value = null
            _uploadedDocumentType.value = null
        }
    }

    // Submit Lead
    fun onSubmitLead(
        vehicleNo: String,
        make: String,
        model: String,
        year: Int,
        chassis: String,
        engine: String,
        ncb: Int,
        claims: Boolean,
        age: Int,
        exp: Int,
        covType: String,
        agencyRepair: Boolean,
        branchId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val leadId = "LA-2026-${(1000..9999).random()}"
            val newLead = Lead(
                id = leadId,
                customerId = "CUST-001",
                customerName = currentCustomerProfile.value.fullName,
                vehicleNumber = vehicleNo,
                vehicleMake = make,
                vehicleModel = model,
                vehicleYear = year,
                chassisNumber = chassis,
                engineNumber = engine,
                status = "PENDING_ACCEPTED",
                ncbPercentage = ncb,
                previousClaims = claims,
                driverAge = age,
                drivingExperienceYears = exp,
                coverageType = covType,
                agencyRepair = agencyRepair,
                preferredBranchId = branchId,
                timestamp = System.currentTimeMillis()
            )
            repo.insertLead(newLead)

            // Issue Broad notification to Insurers
            repo.insertNotification(Notification(
                title = "New Lead in Marketplace",
                body = "Lead $leadId ($make $model, $covType) is now active on the Insurance Marketplace.",
                targetRole = "INSURER"
            ))

            repo.insertNotification(Notification(
                title = "Quote Request Submitted",
                body = "Your request $leadId was successfully submitted. Oman branches and insurance companies are being notified.",
                targetRole = "CUSTOMER",
                targetUserId = "CUST-001"
            ))

            // Pre-seed a greeting message in the Sales Room
            repo.insertChatMessage(ChatMessage(
                leadId = leadId,
                senderId = "SYSTEM",
                senderName = "Affinite System",
                senderRole = "EMPLOYEE",
                messageText = "Welcome to the Secure Sales Room for Request $leadId. Your personal details are completely secure and hidden from insurance companies until you accept a quote.",
                timestamp = System.currentTimeMillis()
            ))

            // Back to customer home
            _activeScreen.value = "customer_home"
        }
    }

    // Customer accepts quote
    fun onAcceptQuotation(leadId: String, quoteId: Int, insurerId: String, insurerName: String, amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val q = repo.getQuotationById(quoteId)
            val l = repo.getLeadById(leadId)
            if (q != null && l != null) {
                // Update quotation
                repo.updateQuotation(q.copy(status = "ACCEPTED"))
                
                // Update lead status
                repo.updateLead(l.copy(status = "POLICY_ISSUED"))

                // Issue System notification
                repo.insertNotification(Notification(
                    title = "Quotation Accepted!",
                    body = "You accepted the quote from $insurerName for OMR $amount. The policy is now being generated.",
                    targetRole = "CUSTOMER",
                    targetUserId = "CUST-001"
                ))

                repo.insertNotification(Notification(
                    title = "Lead Won!",
                    body = "Congratulations, Customer accepted your quote of OMR $amount for Lead $leadId.",
                    targetRole = "INSURER",
                    targetUserId = insurerId
                ))

                repo.insertChatMessage(ChatMessage(
                    leadId = leadId,
                    senderId = "SYSTEM",
                    senderName = "System",
                    senderRole = "EMPLOYEE",
                    messageText = "Quote accepted! Secure payment link generated directly by $insurerName. Customer details are now released to the insurer for Policy Issuance.",
                    timestamp = System.currentTimeMillis()
                ))
            }
        }
    }

    // ==========================================
    // INSURER ACTIONS
    // ==========================================

    // Rule: FIRST THREE Accept Lead
    fun onAcceptLead(leadId: String, insurerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lead = repo.getLeadById(leadId) ?: return@launch
            val acceptedList = if (lead.acceptedByInsurerIds.isEmpty()) {
                mutableListOf()
            } else {
                lead.acceptedByInsurerIds.split(",").toMutableList()
            }

            if (acceptedList.size >= 3) {
                // Lead is already fully occupied
                repo.insertNotification(Notification(
                    title = "Lead Unavailable",
                    body = "Lead $leadId has already been claimed by 3 competing insurers.",
                    targetRole = "INSURER",
                    targetUserId = insurerId
                ))
                return@launch
            }

            if (!acceptedList.contains(insurerId)) {
                acceptedList.add(insurerId)
                val updatedAcceptedStr = acceptedList.joinToString(",")
                val newStatus = if (acceptedList.size >= 3) "COMPETING" else "COMPETING"
                repo.updateLead(lead.copy(
                    acceptedByInsurerIds = updatedAcceptedStr,
                    status = newStatus
                ))

                repo.insertNotification(Notification(
                    title = "Lead Claimed",
                    body = "You successfully claimed Lead $leadId. You are participant #${acceptedList.size} of 3.",
                    targetRole = "INSURER",
                    targetUserId = insurerId
                ))

                repo.insertChatMessage(ChatMessage(
                    leadId = leadId,
                    senderId = insurerId,
                    senderName = "Representative",
                    senderRole = "INSURER",
                    messageText = "We have accepted the lead and are currently compiling a customized premium quote for your $leadId request.",
                    timestamp = System.currentTimeMillis()
                ))
            }
        }
    }

    // Submit Premium Quote
    fun onSubmitQuotation(
        leadId: String,
        insurerId: String,
        insurerName: String,
        premium: Double,
        excess: Double,
        benefits: String,
        exclusions: String,
        addons: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val quote = Quotation(
                leadId = leadId,
                insurerId = insurerId,
                insurerName = insurerName,
                premium = premium,
                excess = excess,
                benefits = benefits,
                exclusions = exclusions,
                addons = addons,
                status = "PENDING",
                timestamp = System.currentTimeMillis()
            )
            repo.insertQuotation(quote)

            // Notify Customer
            repo.insertNotification(Notification(
                title = "New Quote Received",
                body = "$insurerName submitted a competitive quote of OMR $premium for your vehicle.",
                targetRole = "CUSTOMER",
                targetUserId = "CUST-001"
            ))

            repo.insertChatMessage(ChatMessage(
                leadId = leadId,
                senderId = insurerId,
                senderName = insurerName,
                senderRole = "INSURER",
                messageText = "We have submitted our formal comprehensive quote of OMR $premium with OMR $excess deductible.",
                timestamp = System.currentTimeMillis()
            ))

            _activeScreen.value = "insurer_home"
        }
    }

    // Reopen lead after 3 days logic (Triggerable for evaluation)
    fun triggerReopenLead(leadId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lead = repo.getLeadById(leadId) ?: return@launch
            repo.updateLead(lead.copy(
                acceptedByInsurerIds = "",
                status = "REOPENED"
            ))
            repo.insertNotification(Notification(
                title = "Lead Reopened",
                body = "Lead $leadId has been reopened as the customer rejected earlier quotes. New bids invited!",
                targetRole = "INSURER"
            ))
        }
    }

    // Assign to Affinite Custom Quotation Team if no accepts
    fun triggerAssignToAffiniteCustom(leadId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lead = repo.getLeadById(leadId) ?: return@launch
            repo.updateLead(lead.copy(
                status = "ASSIGNED_AFFINITE",
                assignedEmployeeId = "EMP-703"
            ))
            repo.insertNotification(Notification(
                title = "Lead Escallated",
                body = "Lead $leadId has been assigned to Affinite Custom Quotation Team (Operations).",
                targetRole = "EMPLOYEE"
            ))
        }
    }

    // ==========================================
    // CHAT & SUPPORT ACTIONS
    // ==========================================

    fun onSendChatMessage(leadId: String, senderId: String, senderName: String, senderRole: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val msg = ChatMessage(
                leadId = leadId,
                senderId = senderId,
                senderName = senderName,
                senderRole = senderRole,
                messageText = text,
                timestamp = System.currentTimeMillis()
            )
            repo.insertChatMessage(msg)
        }
    }

    fun onAskAiAssistant(prompt: String) {
        if (prompt.isEmpty()) return
        
        viewModelScope.launch {
            _aiChatMessages.value = _aiChatMessages.value + (prompt to true)
            _aiAssistantLoading.value = true

            // Trigger real REST API or simulated Omani helper
            val sysInstruction = "You are Affinite AI, expert motor insurance assistant in Oman. Provide helpful, accurate, and structured advice."
            val response = GeminiApiClient.generateAiContent(prompt, systemInstruction = sysInstruction)
            
            _aiAssistantLoading.value = false
            _aiChatMessages.value = _aiChatMessages.value + (response to false)
        }
    }

    fun onCompareQuotationsAi(leadId: String) {
        viewModelScope.launch {
            _aiAssistantLoading.value = true
            val prompt = "Compare the quotations submitted for lead $leadId. Highlight the differences in premiums, excess, benefits, exclusions, and make a logical recommendation for an Omani driver."
            val response = GeminiApiClient.generateAiContent(prompt)
            _aiAssistantResponse.value = response
            _aiAssistantLoading.value = false
        }
    }

    fun clearAiResponse() {
        _aiAssistantResponse.value = null
    }

    fun onRaiseComplaint(text: String) {
        _selectedComplaintText.value = text
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertNotification(Notification(
                title = "New Customer Complaint",
                body = "Ticket raised by Ahmed Al-Kharusi: '$text'",
                targetRole = "EMPLOYEE"
            ))
            repo.insertNotification(Notification(
                title = "Complaint Registered",
                body = "Your complaint has been successfully logged with ticket #C-${(100..999).random()}. Customer support is reviewing it.",
                targetRole = "CUSTOMER",
                targetUserId = "CUST-001"
            ))
        }
    }

    fun onGlobalSearch(query: String) {
        _searchQuery.value = query
    }
}
