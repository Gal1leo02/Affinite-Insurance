package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import kotlinx.coroutines.launch

// ==========================================
// TRANSLATION ENGINE MAPS
// ==========================================
private val EN = mapOf(
    "app_title" to "Affinite Marketplace",
    "portal_customer" to "Customer Portal",
    "portal_branch" to "Branch Portal",
    "portal_insurer" to "Insurer Portal",
    "portal_employee" to "Employee Portal",
    "portal_admin" to "Super Admin",
    "lang_toggle" to "العربية",
    "active_leads" to "My Active Motor Requests",
    "no_leads" to "No active insurance requests found.",
    "button_submit_req" to "Submit Insurance Request",
    "ocr_card_title" to "AI-Assisted Document Upload",
    "ocr_card_desc" to "Upload your Mulkiya, Driving Licence, or Civil ID. Gemini AI will automatically extract all info.",
    "btn_upload_mulkiya" to "Upload Mulkiya",
    "btn_upload_licence" to "Upload Licence",
    "btn_upload_id" to "Upload Civil ID",
    "form_title" to "Motor Insurance Request Form",
    "form_make" to "Vehicle Make",
    "form_model" to "Vehicle Model",
    "form_year" to "Manufacturing Year",
    "form_plate" to "Plate/Registration Number",
    "form_chassis" to "Chassis Number",
    "form_engine" to "Engine Number",
    "form_ncb" to "No Claim Bonus (%)",
    "form_claims" to "Previous Claims in last 12 months",
    "form_age" to "Driver Age",
    "form_exp" to "Driving Experience (Years)",
    "form_cov_type" to "Coverage Type",
    "form_agency_rep" to "Agency Repair",
    "form_pref_branch" to "Preferred Branch",
    "form_submit" to "Submit Request",
    "quotes_comp" to "Quotation Comparison Matrix",
    "sales_room_title" to "Secure Sales Room Chat",
    "ai_term_desc" to "AI Motor Assistant",
    "chatbot_hint" to "Ask anything about Oman Motor Insurance...",
    "btn_send" to "Send",
    "nearest_branch" to "Recommend Nearest Branch",
    "renewal_reminders" to "Policy Renewal Status"
)

private val AR = mapOf(
    "app_title" to "سوق أفينيتي للتأمين",
    "portal_customer" to "بوابة العملاء",
    "portal_branch" to "بوابة الفرع",
    "portal_insurer" to "بوابة شركة التأمين",
    "portal_employee" to "بوابة الموظف",
    "portal_admin" to "المشرف العام",
    "lang_toggle" to "English",
    "active_leads" to "طلبات تأمين السيارات النشطة",
    "no_leads" to "لم يتم العثور على طلبات تأمين نشطة.",
    "button_submit_req" to "تقديم طلب تأمين جديد",
    "ocr_card_title" to "تحميل المستندات بمساعدة الذكاء الاصطناعي",
    "ocr_card_desc" to "قم بتحميل الملكية، رخصة القيادة، أو البطاقة الشخصية. سيستخرج الذكاء الاصطناعي البيانات تلقائيًا.",
    "btn_upload_mulkiya" to "تحميل الملكية",
    "btn_upload_licence" to "تحميل رخصة القيادة",
    "btn_upload_id" to "تحميل البطاقة المدنية",
    "form_title" to "نموذج طلب تأمين سيارة",
    "form_make" to "صانع المركبة",
    "form_model" to "طراز المركبة",
    "form_year" to "سنة الصنع",
    "form_plate" to "رقم لوحة السيارة",
    "form_chassis" to "رقم الشاصي",
    "form_engine" to "رقم المحرك",
    "form_ncb" to "مكافأة عدم المطالبة (%)",
    "form_claims" to "مطالبات سابقة في آخر ١٢ شهر",
    "form_age" to "عمر السائق",
    "form_exp" to "خبرة القيادة (سنوات)",
    "form_cov_type" to "نوع التغطية",
    "form_agency_rep" to "تصليح الوكالة",
    "form_pref_branch" to "الفرع المفضل",
    "form_submit" to "تقديم الطلب",
    "quotes_comp" to "مصفوفة مقارنة عروض الأسعار",
    "sales_room_title" to "غرفة المبيعات الآمنة والمحادثة",
    "ai_term_desc" to "مساعد السيارات بالذكاء الاصطناعي",
    "chatbot_hint" to "اسأل أي شيء عن تأمين السيارات في عمان...",
    "btn_send" to "إرسال",
    "nearest_branch" to "توصية أقرب فرع",
    "renewal_reminders" to "حالة تجديد بوليصة التأمين"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceMainScreen(viewModel: MarketplaceViewModel) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val currentPortal by viewModel.currentPortal.collectAsStateWithLifecycle()
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    
    val strings = if (isArabic) AR else EN
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme(
        colorScheme = if (isDarkTheme) {
            darkColorScheme(
                primary = Color(0xFFADC6FF),
                onPrimary = Color(0xFF003159),
                primaryContainer = Color(0xFF00477D),
                onPrimaryContainer = Color(0xFFD1E4FF),
                secondary = Color(0xFFBFC6DC),
                onSecondary = Color(0xFF293041),
                secondaryContainer = Color(0xFF2B313A),
                onSecondaryContainer = Color(0xFFE2E2E6),
                background = Color(0xFF111418),
                onBackground = Color(0xFFE2E2E6),
                surface = Color(0xFF1A1C1E),
                onSurface = Color(0xFFE2E2E6),
                outline = Color(0xFF44474E),
                outlineVariant = Color(0xFF535F70)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF0061A4),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFD1E4FF),
                onPrimaryContainer = Color(0xFF001D36),
                secondary = Color(0xFF535F70),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFF2F0F4),
                onSecondaryContainer = Color(0xFF1A1C1E),
                background = Color(0xFFF8F9FF),
                onBackground = Color(0xFF1A1C1E),
                surface = Color.White,
                onSurface = Color(0xFF1A1C1E),
                outline = Color(0xFFDEE2EB),
                outlineVariant = Color(0xFFADC6FF)
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "A",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                            Column {
                                Text(
                                    text = "Affinite",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Insurance Marketplace",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                        }
                    },
                    actions = {
                        // Portal / Role Swapper Menu Trigger
                        var showPortalMenu by remember { mutableStateOf(false) }
                        Button(
                            onClick = { showPortalMenu = true },
                            modifier = Modifier.testTag("portal_swapper_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Filled.AccountCircle, contentDescription = "Role")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentPortal) {
                                    "CUSTOMER" -> strings["portal_customer"] ?: "Customer Portal"
                                    "BRANCH" -> strings["portal_branch"] ?: "Branch Portal"
                                    "INSURER" -> strings["portal_insurer"] ?: "Insurer Portal"
                                    "EMPLOYEE" -> strings["portal_employee"] ?: "Employee Portal"
                                    else -> strings["portal_admin"] ?: "Super Admin"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showPortalMenu,
                            onDismissRequest = { showPortalMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings["portal_customer"] ?: "Customer Portal") },
                                onClick = { viewModel.switchPortal("CUSTOMER"); showPortalMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text(strings["portal_branch"] ?: "Branch Portal") },
                                onClick = { viewModel.switchPortal("BRANCH"); showPortalMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text(strings["portal_insurer"] ?: "Insurer Portal") },
                                onClick = { viewModel.switchPortal("INSURER"); showPortalMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text(strings["portal_employee"] ?: "Employee Portal") },
                                onClick = { viewModel.switchPortal("EMPLOYEE"); showPortalMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text(strings["portal_admin"] ?: "Super Admin") },
                                onClick = { viewModel.switchPortal("ADMIN"); showPortalMenu = false }
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Language Button
                        IconButton(
                            onClick = { viewModel.toggleLanguage() },
                            modifier = Modifier.testTag("lang_toggle_button")
                        ) {
                            Icon(Icons.Filled.Language, contentDescription = "Language")
                        }

                        // Theme Toggle
                        IconButton(
                            onClick = { viewModel.toggleTheme() },
                            modifier = Modifier.testTag("theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Theme"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                // Main Multi-Portal Route Controller
                Crossfade(targetState = currentPortal, label = "portal") { portal ->
                    when (portal) {
                        "CUSTOMER" -> CustomerPortalScreen(viewModel, strings)
                        "BRANCH" -> BranchPortalScreen(viewModel, strings)
                        "INSURER" -> InsurerPortalScreen(viewModel, strings)
                        "EMPLOYEE" -> EmployeePortalScreen(viewModel, strings)
                        "ADMIN" -> AdminPortalScreen(viewModel, strings)
                    }
                }

                // Global Floating AI Assistant Chat Button and Panel
                FloatingAiAssistantBubble(viewModel, strings)
            }
        }
    }
}

// ==========================================
// PORTAL SCREEN 1: CUSTOMER PORTAL
// ==========================================
@Composable
fun CustomerPortalScreen(viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val leadsList by viewModel.currentCustomerLeads.collectAsStateWithLifecycle(emptyList())
    val selectedLeadId by viewModel.currentLeadId.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (activeScreen == "customer_home") {
            // Customer Header Hero Banner (Geometric Slate Blue theme)
            CustomerHeroSection(strings)

            Spacer(modifier = Modifier.height(16.dp))

            // AI Status Banner (Geometric Balance style)
            AiStatusBanner(strings)

            Spacer(modifier = Modifier.height(16.dp))

            // AI OCR Document Upload Panel
            OcrUploadSection(viewModel, strings)

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Services Grid (Geometric Balance style)
            QuickServicesGrid(viewModel)

            Spacer(modifier = Modifier.height(20.dp))

            // Active Leads Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings["active_leads"] ?: "My Active Motor Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { viewModel.setScreen("customer_form") },
                    modifier = Modifier.testTag("request_insurance_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings["button_submit_req"] ?: "Submit Request")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (leadsList.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            strings["no_leads"] ?: "No active insurance requests found.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                leadsList.forEach { lead ->
                    CustomerLeadCard(lead, viewModel, strings)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recent Timeline (Geometric Balance style)
            RecentTimeline()

            Spacer(modifier = Modifier.height(16.dp))

            // Google Maps Branch Locator
            MapsBranchLocatorSection(viewModel, strings)

        } else if (activeScreen == "customer_form") {
            InsuranceRequestForm(viewModel, strings)
        } else if (activeScreen == "customer_sales_room" && selectedLeadId != null) {
            SalesRoomScreen(viewModel, selectedLeadId!!, strings)
        }
    }
}

@Composable
fun CustomerHeroSection(strings: Map<String, String>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val brush = Brush.linearGradient(
                        colors = listOf(primaryColor, containerColor),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                    drawRect(brush = brush)
                }
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Ahmed bin Salim Al-Kharusi",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Welcome to Affinite Oman Digital Insurance Marketplace. Complete your ROP profile, compare quotes side-by-side, and obtain policy issuance instantly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun AiStatusBanner(strings: Map<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFADC6FF)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1E4FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("✨", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Gemini AI Assistant",
                    color = Color(0xFF001D36),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "3 documents verified via OCR. Ready for quotation.",
                    color = Color(0xFF001D36).copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun QuickServicesGrid(viewModel: MarketplaceViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Service 1: New Policy
        Card(
            modifier = Modifier
                .weight(1f)
                .height(112.dp)
                .clickable { viewModel.setScreen("customer_form") },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F0F4)),
            border = BorderStroke(1.dp, Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("📄", fontSize = 24.sp)
                Column {
                    Text(
                        text = "New Policy",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "Instant OCR upload",
                        fontSize = 10.sp,
                        color = Color(0xFF44474E)
                    )
                }
            }
        }

        // Service 2: Renewals
        Card(
            modifier = Modifier
                .weight(1f)
                .height(112.dp)
                .clickable { /* No action needed */ },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F0F4)),
            border = BorderStroke(1.dp, Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("🛡️", fontSize = 24.sp)
                Column {
                    Text(
                        text = "Renewals",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "1 expiring soon",
                        fontSize = 10.sp,
                        color = Color(0xFF44474E)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentTimeline() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFDEE2EB)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RECENT ACTIVITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF44474E),
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Activity Item 1
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(32.dp)
                        .background(Color(0xFF0061A4), shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Mulkiya Verified",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "Today, 10:45 AM via Gemini OCR",
                        fontSize = 10.sp,
                        color = Color(0xFF74777F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Item 2 (opacity 50%)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.5f)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(32.dp)
                        .background(Color(0xFF74777F), shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Request Submitted",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "Yesterday, 4:20 PM",
                        fontSize = 10.sp,
                        color = Color(0xFF74777F)
                    )
                }
            }
        }
    }
}

@Composable
fun OcrUploadSection(viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    val ocrProgress by viewModel.ocrProgress.collectAsStateWithLifecycle()
    val extractedOcrData by viewModel.extractedOcrData.collectAsStateWithLifecycle()
    val uploadedDocumentType by viewModel.uploadedDocumentType.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                strings["ocr_card_title"] ?: "AI-Assisted Document Upload",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                strings["ocr_card_desc"] ?: "Upload document and let Gemini AI automatically extract all details.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (ocrProgress) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Gemini AI is scanning $uploadedDocumentType and extracting Omani vehicle registration details...")
                }
            } else if (extractedOcrData != null) {
                // OCR Confirmation Dialog View
                OcrConfirmationDialog(extractedOcrData!!, viewModel, strings)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.onUploadDocument("MULKIYA", "mock_base64_mulkiya") },
                        modifier = Modifier.testTag("upload_mulkiya_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Filled.DirectionsCar, contentDescription = "Mulkiya")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings["btn_upload_mulkiya"] ?: "Upload Mulkiya")
                    }
                    Button(
                        onClick = { viewModel.onUploadDocument("LICENCE", "mock_base64_licence") },
                        modifier = Modifier.testTag("upload_licence_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Filled.Assignment, contentDescription = "Licence")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings["btn_upload_licence"] ?: "Upload Licence")
                    }
                }
            }
        }
    }
}

@Composable
fun OcrConfirmationDialog(
    data: Map<String, String>,
    viewModel: MarketplaceViewModel,
    strings: Map<String, String>
) {
    var editMake by remember { mutableStateOf(data["vehicleMake"] ?: "") }
    var editModel by remember { mutableStateOf(data["vehicleModel"] ?: "") }
    var editYear by remember { mutableStateOf(data["vehicleYear"] ?: "") }
    var editPlate by remember { mutableStateOf(data["vehicleNumber"] ?: "") }
    var editChassis by remember { mutableStateOf(data["chassisNumber"] ?: "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Extracted", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Extracted OCR Information:", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = editPlate,
                onValueChange = { editPlate = it },
                label = { Text("Plate / Reg Number") },
                modifier = Modifier.fillMaxWidth().testTag("edit_plate_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = editMake,
                    onValueChange = { editMake = it },
                    label = { Text("Vehicle Make") },
                    modifier = Modifier.weight(1f).testTag("edit_make_input")
                )
                TextField(
                    value = editModel,
                    onValueChange = { editModel = it },
                    label = { Text("Model") },
                    modifier = Modifier.weight(1f).testTag("edit_model_input")
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = editChassis,
                onValueChange = { editChassis = it },
                label = { Text("Chassis Number") },
                modifier = Modifier.fillMaxWidth().testTag("edit_chassis_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { viewModel.onConfirmOcrDetails(emptyMap()) }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.onConfirmOcrDetails(mapOf(
                            "vehicleMake" to editMake,
                            "vehicleModel" to editModel,
                            "vehicleYear" to editYear,
                            "vehicleNumber" to editPlate,
                            "chassisNumber" to editChassis
                        ))
                    },
                    modifier = Modifier.testTag("confirm_ocr_button")
                ) {
                    Text("Confirm & Save Vehicle")
                }
            }
        }
    }
}

@Composable
fun CustomerLeadCard(lead: Lead, viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(32.dp))) {
            // Decorative geometric circle in bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 16.dp, y = 16.dp)
                    .size(96.dp)
                    .background(color = Color(0xFFD1E4FF).copy(alpha = 0.25f), shape = CircleShape)
            )

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFD1E4FF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Active Request",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0061A4)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${lead.vehicleMake} ${lead.vehicleModel}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Manufacturing Year: ${lead.vehicleYear}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    // Lead Status Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (lead.status) {
                                    "PENDING_ACCEPTED" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                    "COMPETING" -> Color(0xFF00B0FF).copy(alpha = 0.2f)
                                    "POLICY_ISSUED" -> Color(0xFF00C853).copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = lead.status,
                            color = when (lead.status) {
                                "PENDING_ACCEPTED" -> Color(0xFFE65100)
                                "COMPETING" -> Color(0xFF01579B)
                                "POLICY_ISSUED" -> Color(0xFF1B5E20)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Timeline Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimelineStep("Requested", isDone = true)
                    TimelineArrow()
                    TimelineStep("Bidding", isDone = lead.status != "PENDING_ACCEPTED")
                    TimelineArrow()
                    TimelineStep("Issued", isDone = lead.status == "POLICY_ISSUED")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // AXA / GIG mock badge stack
                    Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFF1F0F7), shape = CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AXA", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFE1E2EC), shape = CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("GIG", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFD1E4FF), shape = CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+3", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0061A4))
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.selectLead(lead.id)
                            viewModel.setScreen("customer_sales_room")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("enter_sales_room_${lead.id}")
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = "Sales Room", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compare Quotes")
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineStep(label: String, isDone: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isDone) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Filled.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun TimelineArrow() {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(2.dp)
            .background(Color.Gray.copy(alpha = 0.3f))
    )
}

// ==========================================
// PORTAL SCREEN 2: INSURANCE BRANCH PORTAL
// ==========================================
@Composable
fun BranchPortalScreen(viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    val leadsList by viewModel.leads.collectAsStateWithLifecycle()
    val selectedLeadId by viewModel.currentLeadId.collectAsStateWithLifecycle()
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (activeScreen == "branch_home") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Store, contentDescription = "Branch", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Assigned Branch Workspace: Muscat Head Office", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Review active leads, coordinates with Head Office and chat in the Sales Room securely.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

            Spacer(modifier = Modifier.height(16.dp))

            // Performance metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricWidget(title = "Assigned Leads", value = "12", modifier = Modifier.weight(1f))
                MetricWidget(title = "Conversions", value = "78%", modifier = Modifier.weight(1f))
                MetricWidget(title = "Avg SLA (mins)", value = "18", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Assigned Customer Enquiries:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            leadsList.forEach { lead ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(1.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = lead.customerName, fontWeight = FontWeight.Bold)
                                Text(text = "Vehicle: ${lead.vehicleMake} ${lead.vehicleModel} (${lead.vehicleYear})", fontSize = 13.sp)
                            }
                            Text(text = "Status: ${lead.status}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    viewModel.selectLead(lead.id)
                                    viewModel.setScreen("branch_sales_room")
                                },
                                modifier = Modifier.testTag("branch_sales_room_button_${lead.id}")
                            ) {
                                Text("Open Secure Sales Room")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else if (activeScreen == "branch_sales_room" && selectedLeadId != null) {
            SalesRoomScreen(viewModel, selectedLeadId!!, strings)
        }
    }
}

@Composable
fun MetricWidget(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

// ==========================================
// PORTAL SCREEN 3: INSURANCE COMPANY PORTAL
// ==========================================
@Composable
fun InsurerPortalScreen(viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    val leadsList by viewModel.leads.collectAsStateWithLifecycle()
    val currentInsurerProfile by viewModel.currentInsurerProfile.collectAsStateWithLifecycle(viewModel.currentInsurers[0])
    val selectedLeadId by viewModel.currentLeadId.collectAsStateWithLifecycle()
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (activeScreen == "insurer_home") {
            // Insurer context switcher (for easy demonstration of multiple bidders)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Select Bidding Insurance Company (Simulating 4 Competing Insurers):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("NLG", "Oman Ins", "Muscat Ins", "Dhofar").forEachIndexed { i, name ->
                            ElevatedFilterChip(
                                selected = viewModel.activeInsurerIndex.value == i,
                                onClick = { viewModel.setInsurerIndex(i) },
                                label = { Text(name) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Business, contentDescription = "Insurer", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${currentInsurerProfile.fullName} Portal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Oman PDPL Compliant Portal: Customer contact details remain hidden until quotation acceptance.", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Public Marketplace Leads (ROP Verified):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            leadsList.forEach { lead ->
                val acceptedInsurers = if (lead.acceptedByInsurerIds.isEmpty()) emptyList() else lead.acceptedByInsurerIds.split(",")
                val selfAccepted = acceptedInsurers.contains(currentInsurerProfile.insurerId)
                val isOccupied = acceptedInsurers.size >= 3

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Lead ID: ${lead.id}", fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "Claims: ${if (lead.previousClaims) "Yes" else "No"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Vehicle: ${lead.vehicleMake} ${lead.vehicleModel} (${lead.vehicleYear})", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Coverage Preferred: ${lead.coverageType}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "No Claim Bonus: ${lead.ncbPercentage}%", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Active Claimants: ${acceptedInsurers.size}/3", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            // First 3 Click Lead Distribution Logic
                            if (!selfAccepted) {
                                Button(
                                    onClick = { viewModel.onAcceptLead(lead.id, currentInsurerProfile.insurerId!!) },
                                    enabled = !isOccupied && lead.status != "POLICY_ISSUED",
                                    modifier = Modifier.testTag("claim_lead_button_${lead.id}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isOccupied) Color.Gray else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(if (isOccupied) "Lead Occupied (Max 3)" else "Accept Lead & Compete")
                                }
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.selectLead(lead.id)
                                        viewModel.setScreen("insurer_submit_quote")
                                    },
                                    modifier = Modifier.testTag("submit_quote_btn_${lead.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("Submit Bid / Quote")
                                }
                            }

                            // Sales Room button if accepted
                            if (selfAccepted) {
                                TextButton(
                                    onClick = {
                                        viewModel.selectLead(lead.id)
                                        viewModel.setScreen("insurer_sales_room")
                                    },
                                    modifier = Modifier.testTag("insurer_sales_room_link_${lead.id}")
                                ) {
                                    Icon(Icons.Filled.Chat, contentDescription = "Chat", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Enter Sales Room")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else if (activeScreen == "insurer_submit_quote" && selectedLeadId != null) {
            QuoteSubmissionForm(viewModel, selectedLeadId!!, currentInsurerProfile)
        } else if (activeScreen == "insurer_sales_room" && selectedLeadId != null) {
            SalesRoomScreen(viewModel, selectedLeadId!!, strings)
        }
    }
}

@Composable
fun QuoteSubmissionForm(viewModel: MarketplaceViewModel, leadId: String, insurerProfile: UserProfile) {
    var premium by remember { mutableStateOf("280") }
    var excess by remember { mutableStateOf("50") }
    var benefits by remember { mutableStateOf("ROP Road Side Assistance, UAE Geographical Extension, Replacement Car (7 days)") }
    var exclusions by remember { mutableStateOf("Off road desert racing driving, unlicensed drivers") }
    var addons by remember { mutableStateOf("Windscreen cover (+OMR 10), Full Agency Repair (+OMR 40)") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Quotation Submission Engine: Lead $leadId", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = premium,
                onValueChange = { premium = it },
                label = { Text("Base Premium (OMR)") },
                modifier = Modifier.fillMaxWidth().testTag("bid_premium_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = excess,
                onValueChange = { excess = it },
                label = { Text("Excess / Deductible Amount (OMR)") },
                modifier = Modifier.fillMaxWidth().testTag("bid_excess_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = benefits,
                onValueChange = { benefits = it },
                label = { Text("Covered Benefits (Comma separated)") },
                modifier = Modifier.fillMaxWidth().testTag("bid_benefits_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = exclusions,
                onValueChange = { exclusions = it },
                label = { Text("Exclusions (Comma separated)") },
                modifier = Modifier.fillMaxWidth().testTag("bid_exclusions_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = addons,
                onValueChange = { addons = it },
                label = { Text("Optional Add-on covers") },
                modifier = Modifier.fillMaxWidth().testTag("bid_addons_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { viewModel.setScreen("insurer_home") }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.onSubmitQuotation(
                            leadId = leadId,
                            insurerId = insurerProfile.insurerId ?: "MIC",
                            insurerName = insurerProfile.fullName,
                            premium = premium.toDoubleOrNull() ?: 280.0,
                            excess = excess.toDoubleOrNull() ?: 50.0,
                            benefits = benefits,
                            exclusions = exclusions,
                            addons = addons
                        )
                    },
                    modifier = Modifier.testTag("submit_quote_bid_btn")
                ) {
                    Text("Submit Quote Bid")
                }
            }
        }
    }
}

// ==========================================
// PORTAL SCREEN 4: AFFINITE EMPLOYEE PORTAL
// ==========================================
@Composable
fun EmployeePortalScreen(viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    val activeDepartment by viewModel.activeDepartment.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Badge, contentDescription = "Employee", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Affinite Brokerage Command Center", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Department navigation chips
        ScrollableTabRow(
            selectedTabIndex = when (activeDepartment) {
                "SALES" -> 0
                "OPERATIONS" -> 1
                "ACCOUNTS" -> 2
                "SUPPORT" -> 3
                "RENEWALS" -> 4
                "HR" -> 5
                else -> 6
            },
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("SALES", "OPERATIONS", "ACCOUNTS", "SUPPORT", "RENEWALS", "HR", "MANAGEMENT").forEach { dept ->
                Tab(
                    selected = activeDepartment == dept,
                    onClick = { viewModel.setDepartment(dept) },
                    text = { Text(dept, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Department specific sub-dashboards
        when (activeDepartment) {
            "SALES" -> SalesDeptDashboard(viewModel)
            "OPERATIONS" -> OperationsDeptDashboard(viewModel)
            "ACCOUNTS" -> AccountsDeptDashboard()
            "SUPPORT" -> SupportDeptDashboard(viewModel)
            "RENEWALS" -> RenewalsDeptDashboard()
            "HR" -> HrDeptDashboard()
            "MANAGEMENT" -> ManagementDeptDashboard()
        }
    }
}

@Composable
fun SalesDeptDashboard(viewModel: MarketplaceViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Sales Pipeline Monitoring:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MetricWidget(title = "New Leads", value = "19", modifier = Modifier.weight(1f))
                MetricWidget(title = "Pipeline Vol", value = "OMR 24,900", modifier = Modifier.weight(1f))
                MetricWidget(title = "Est Revenue", value = "OMR 3,735", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas drawing of Sales Funnel conversion
            Text("Sales Funnel Conversion Analytics:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Funnel layers
                    val layerColors = listOf(Color(0xFF00C853), Color(0xFF0091EA), Color(0xFFFFD600), Color(0xFFFF1744))
                    val layerNames = listOf("Impressions (100%)", "Submitted (68%)", "Competing (35%)", "Won policies (12%)")

                    for (i in 0 until 4) {
                        val currentY = i * (height / 4)
                        val startX = i * 40f
                        val endX = width - (i * 40f)
                        drawRect(
                            color = layerColors[i].copy(alpha = 0.8f),
                            topLeft = Offset(startX, currentY + 4f),
                            size = Size(endX - startX, (height / 4) - 8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OperationsDeptDashboard(viewModel: MarketplaceViewModel) {
    val leadsList by viewModel.leads.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Operations & Document Verification Queue:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            leadsList.forEach { lead ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(lead.customerName, fontWeight = FontWeight.SemiBold)
                        Text("Vehicle: ${lead.vehicleMake} (Chassis: ${lead.chassisNumber})", fontSize = 12.sp)
                    }

                    Row {
                        Button(
                            onClick = { viewModel.onConfirmOcrDetails(emptyMap()) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("ops_verify_btn_${lead.id}")
                        ) {
                            Text("Verify OCR Documents", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountsDeptDashboard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Invoicing & Brokerage Settlements (Oman ROP / Tax):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MetricWidget(title = "Brokerage Due", value = "OMR 4,910", modifier = Modifier.weight(1f))
                MetricWidget(title = "Settled (MTD)", value = "OMR 2,401", modifier = Modifier.weight(1f))
                MetricWidget(title = "Unpaid Invoices", value = "14", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SupportDeptDashboard(viewModel: MarketplaceViewModel) {
    val complaintText by viewModel.selectedComplaintText.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Active Support & Complaint Tickets:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (complaintText != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red.copy(alpha = 0.05f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Ticket: #C-${(100..999).random()} - Ahmed Al-Kharusi", fontWeight = FontWeight.Bold)
                        Text(complaintText!!)
                    }
                }
            } else {
                Text("No open customer complaints or unresolved support tickets.", color = Color.Gray)
            }
        }
    }
}

@Composable
fun RenewalsDeptDashboard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Automated Renewal Campaign Optimizer:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Policies Expiring in next 30 days:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                "Salim bin Juma Al-Balushi - Toyota Land Cruiser (Expiring in 4 days)",
                "Maryam Al-Riyami - Nissan Patrol (Expiring in 11 days)",
                "Khalid Al-Abri - Honda Accord (Expiring in 24 days)"
            ).forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item, fontSize = 12.sp)
                    Text("Trigger Campaign", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable {})
                }
            }
        }
    }
}

@Composable
fun HrDeptDashboard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Employee Leaderboard & SLA Productivity:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            listOf(
                Triple("Salim Al-Harthy", "Muscat Head Office", "SLA: 12m | Conversion: 91%"),
                Triple("Muna Al-Hashmi", "Sohar Branch", "SLA: 15m | Conversion: 84%"),
                Triple("Zahir Al-Rasbi", "Salalah Branch", "SLA: 19m | Conversion: 79%")
            ).forEachIndexed { index, triple ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("#${index + 1}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(triple.first, fontWeight = FontWeight.SemiBold)
                            Text(triple.second, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Text(triple.third, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ManagementDeptDashboard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("C-Suite Intelligence Insights (OMR Volume):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MetricWidget(title = "Active Policies", value = "1,921", modifier = Modifier.weight(1f))
                MetricWidget(title = "Premium Volume", value = "OMR 512.9K", modifier = Modifier.weight(1f))
                MetricWidget(title = "Monthly Growth", value = "+18.4%", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Market Share distribution across Oman Insurers:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            // Canvas Drawing of Market share pie chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val segments = listOf(35f, 25f, 20f, 20f)
                    val colors = listOf(Color(0xFF00C853), Color(0xFF0091EA), Color(0xFFFFD600), Color(0xFFFF1744))
                    var startAngle = 0f
                    for (i in segments.indices) {
                        val sweepAngle = segments[i] * 3.6f
                        drawArc(
                            color = colors[i],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 24f, cap = StrokeCap.Round)
                        )
                        startAngle += sweepAngle
                    }
                }
            }
        }
    }
}

// ==========================================
// PORTAL SCREEN 5: SUPER ADMIN PORTAL
// ==========================================
@Composable
fun AdminPortalScreen(viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.SettingsInputComponent, contentDescription = "Admin", tint = Color.Red, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Super Admin & Cybersecurity Dashboard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("Real-Time Infrastructure Health (Direct SQLite Monitoring):", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MetricWidget(title = "Database Load", value = "2.4%", modifier = Modifier.weight(1f))
                MetricWidget(title = "API Status", value = "99.99%", modifier = Modifier.weight(1f))
                MetricWidget(title = "JWT Handshakes", value = "832/sec", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Database Load Metric Trend (Canvas Graph):", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            // Graph canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Black.copy(alpha = 0.05f))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val points = listOf(10f, 30f, 15f, 45f, 32f, 60f, 40f, 80f, 90f)
                    val widthStep = size.width / (points.size - 1)
                    val height = size.height

                    for (i in 0 until points.size - 1) {
                        val startX = i * widthStep
                        val startY = height - (points[i] / 100f * height)
                        val endX = (i + 1) * widthStep
                        val endY = height - (points[i + 1] / 100f * height)

                        drawLine(
                            color = Color.Red,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 4f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Oman PDPL Compliance & Encryption status:", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, contentDescription = "Encrypted", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Database fields: AES-256 Encrypted. TLS 1.3 Active.", fontSize = 12.sp)
            }
        }
    }
}

// ==========================================
// SECURE SALES ROOM CHAT SCREEN
// ==========================================
@Composable
fun SalesRoomScreen(viewModel: MarketplaceViewModel, leadId: String, strings: Map<String, String>) {
    val messages by viewModel.currentLeadMessages.collectAsStateWithLifecycle(emptyList())
    val quotations by viewModel.currentLeadQuotations.collectAsStateWithLifecycle(emptyList())
    val currentPortal by viewModel.currentPortal.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiAssistantResponse.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiAssistantLoading.collectAsStateWithLifecycle()

    var chatText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sales Room: Request $leadId",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    viewModel.setScreen(
                        when (currentPortal) {
                            "CUSTOMER" -> "customer_home"
                            "BRANCH" -> "branch_home"
                            "INSURER" -> "insurer_home"
                            else -> "customer_home"
                        }
                    )
                }
            ) {
                Text("Exit Sales Room")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quotation Comparison matrix visible inside Sales Room for customer comparison
        if (quotations.isNotEmpty() && currentPortal == "CUSTOMER") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Quotes Submitted by Insurers (Quotation Engine):", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    quotations.forEach { quote ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.02f))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(quote.insurerName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Premium: OMR ${quote.premium} | Excess: OMR ${quote.excess}", fontSize = 11.sp, color = Color.Gray)
                                Text("Benefits: ${quote.benefits}", fontSize = 11.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
                            }
                            if (quote.status == "ACCEPTED") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Green.copy(alpha = 0.2f))
                                        .padding(4.dp)
                                ) {
                                    Text("Accepted", color = Color.Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.onAcceptQuotation(leadId, quote.id, quote.insurerId, quote.insurerName, quote.premium) },
                                    modifier = Modifier.testTag("accept_quote_btn_${quote.id}")
                                ) {
                                    Text("Accept Quote")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // AI Quote Comparison Trigger
                    if (aiLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else if (aiResponse != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = "AI", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Gemini AI Quote Analysis:", fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(aiResponse!!, fontSize = 12.sp)
                                TextButton(onClick = { viewModel.clearAiResponse() }) {
                                    Text("Clear")
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.onCompareQuotationsAi(leadId) },
                            modifier = Modifier.testTag("ai_compare_quotes_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = "AI")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Let Gemini Compare Quotes & Recommend")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Chat Message Log
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    val isSelf = msg.senderRole == currentPortal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelf) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(msg.senderName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(msg.messageText, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = chatText,
                onValueChange = { chatText = it },
                placeholder = { Text("Type secure message...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text_field")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (chatText.isNotEmpty()) {
                        viewModel.onSendChatMessage(
                            leadId = leadId,
                            senderId = when (currentPortal) {
                                "CUSTOMER" -> "CUST-001"
                                "BRANCH" -> "MCT-01"
                                "INSURER" -> "OIC"
                                else -> "EMP-1"
                            },
                            senderName = when (currentPortal) {
                                "CUSTOMER" -> "Ahmed Al-Kharusi"
                                "BRANCH" -> "Salim (Muscat Head Office)"
                                "INSURER" -> "Oman Insurance Co"
                                else -> "Affinite Rep"
                            },
                            senderRole = currentPortal,
                            text = chatText
                        )
                        chatText = ""
                    }
                },
                modifier = Modifier.testTag("send_chat_msg_btn")
            ) {
                Text("Send")
            }
        }
    }
}

// ==========================================
// GOOGLE MAPS & BRANCH RECOM SCREEN
// ==========================================
@Composable
fun MapsBranchLocatorSection(viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    val branchesList by viewModel.branches.collectAsStateWithLifecycle()
    val selectedBranch by viewModel.selectedMapBranch.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                strings["nearest_branch"] ?: "Recommend Nearest Branch",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Google Maps Canvas Drawing representation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0F2F1)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw grid representation of map
                    val width = size.width
                    val height = size.height

                    // Drawing mock ROP Oman roads
                    drawLine(Color.White, Offset(0f, height / 2), Offset(width, height / 2), strokeWidth = 24f)
                    drawLine(Color.White, Offset(width / 3, 0f), Offset(width / 3, height), strokeWidth = 24f)

                    // Draw Muscat Head Office dot (at coordinates)
                    drawCircle(Color(0xFF0091EA), radius = 18f, center = Offset(width / 3, height / 2))
                    drawCircle(Color.White, radius = 6f, center = Offset(width / 3, height / 2))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = selectedBranch?.name ?: "Muscat Head Office",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = selectedBranch?.locationName ?: "Al Khuwair, Muscat",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Oman Branch Network:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))

            branchesList.forEach { branch ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectMapBranch(branch) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(branch.name, fontSize = 13.sp)
                    Icon(
                        imageVector = if (selectedBranch?.id == branch.id) Icons.Filled.LocationOn else Icons.Outlined.LocationOn,
                        contentDescription = "Loc",
                        tint = if (selectedBranch?.id == branch.id) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}

// ==========================================
// INSURANCE REQUEST COMPREHENSIVE FORM
// ==========================================
@Composable
fun InsuranceRequestForm(viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    var make by remember { mutableStateOf("Toyota") }
    var model by remember { mutableStateOf("Land Cruiser") }
    var year by remember { mutableStateOf("2023") }
    var plate by remember { mutableStateOf("8310 AR") }
    var chassis by remember { mutableStateOf("MJDH3920192A021") }
    var engine by remember { mutableStateOf("EG-TY-389102") }
    var ncb by remember { mutableStateOf("30") }
    var age by remember { mutableStateOf("32") }
    var experience by remember { mutableStateOf("8") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(strings["form_title"] ?: "Motor Insurance Request Form", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = make,
                onValueChange = { make = it },
                label = { Text(strings["form_make"] ?: "Vehicle Make") },
                modifier = Modifier.fillMaxWidth().testTag("form_make_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = model,
                onValueChange = { model = it },
                label = { Text(strings["form_model"] ?: "Vehicle Model") },
                modifier = Modifier.fillMaxWidth().testTag("form_model_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text(strings["form_year"] ?: "Year") },
                    modifier = Modifier.weight(1f).testTag("form_year_input")
                )
                TextField(
                    value = plate,
                    onValueChange = { plate = it },
                    label = { Text(strings["form_plate"] ?: "Plate Number") },
                    modifier = Modifier.weight(1f).testTag("form_plate_input")
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = chassis,
                onValueChange = { chassis = it },
                label = { Text(strings["form_chassis"] ?: "Chassis Number") },
                modifier = Modifier.fillMaxWidth().testTag("form_chassis_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = ncb,
                    onValueChange = { ncb = it },
                    label = { Text(strings["form_ncb"] ?: "NCB (%)") },
                    modifier = Modifier.weight(1f).testTag("form_ncb_input")
                )
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text(strings["form_age"] ?: "Driver Age") },
                    modifier = Modifier.weight(1f).testTag("form_age_input")
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = experience,
                onValueChange = { experience = it },
                label = { Text(strings["form_exp"] ?: "Experience (Years)") },
                modifier = Modifier.fillMaxWidth().testTag("form_exp_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { viewModel.setScreen("customer_home") }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.onSubmitLead(
                            vehicleNo = plate,
                            make = make,
                            model = model,
                            year = year.toIntOrNull() ?: 2023,
                            chassis = chassis,
                            engine = engine,
                            ncb = ncb.toIntOrNull() ?: 30,
                            claims = false,
                            age = age.toIntOrNull() ?: 32,
                            exp = experience.toIntOrNull() ?: 8,
                            covType = "Comprehensive",
                            agencyRepair = true,
                            branchId = "MCT-01"
                        )
                    },
                    modifier = Modifier.testTag("submit_form_confirm_button")
                ) {
                    Text(strings["form_submit"] ?: "Submit Request")
                }
            }
        }
    }
}

// ==========================================
// FLOATING AI ASSISTANT CHAT COMPONENT
// ==========================================
@Composable
fun FloatingAiAssistantBubble(viewModel: MarketplaceViewModel, strings: Map<String, String>) {
    var expanded by remember { mutableStateOf(false) }
    val chatMessages by viewModel.aiChatMessages.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiAssistantLoading.collectAsStateWithLifecycle()
    var promptInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(horizontalAlignment = Alignment.End) {
            if (expanded) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .height(400.dp)
                        .padding(bottom = 8.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // AI Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = "AI", tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings["ai_term_desc"] ?: "AI Motor Assistant", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { expanded = false }) {
                                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        // Message List
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(12.dp),
                            reverseLayout = false
                        ) {
                            items(chatMessages) { chat ->
                                val isUser = chat.second
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isUser) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Text(
                                            text = chat.first,
                                            modifier = Modifier.padding(10.dp),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (aiLoading) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            }
                        }

                        // Quick helpers buttons for user
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { viewModel.onAskAiAssistant("Explain Motor NCB (No Claim Bonus)") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("What is NCB?", fontSize = 10.sp)
                            }
                            Button(
                                onClick = { viewModel.onAskAiAssistant("What is third party only insurance?") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Third Party?", fontSize = 10.sp)
                            }
                        }

                        // Chat Input
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = promptInput,
                                onValueChange = { promptInput = it },
                                placeholder = { Text(strings["chatbot_hint"] ?: "Ask AI...", fontSize = 12.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("floating_ai_prompt_field"),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = {
                                    if (promptInput.isNotEmpty()) {
                                        viewModel.onAskAiAssistant(promptInput)
                                        promptInput = ""
                                    }
                                },
                                modifier = Modifier.testTag("floating_ai_send_button")
                            ) {
                                Icon(Icons.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.testTag("floating_ai_toggle_bubble"),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = "AI Assistant")
            }
        }
    }
}
