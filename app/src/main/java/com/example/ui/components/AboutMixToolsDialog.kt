package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.AppLanguage

private const val DEVELOPER_URL = "https://annaem143.blogspot.com/?m=1"

@Composable
fun AboutMixToolsDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val openDeveloperBlog = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DEVELOPER_URL))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "ওয়েবসাইট ওপেন করা যায়নি", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp)
                .testTag("about_mixtools_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Top Hero Gradient Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            color = Color.White,
                            shadowElevation = 6.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_mixtools_logo),
                                    contentDescription = "MixTools Logo",
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "MixTools",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "ভার্সন ${com.example.BuildConfig.VERSION_NAME} • অফিসিয়াল রিলিজ" else "Version ${com.example.BuildConfig.VERSION_NAME} • Official Release",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "আপনার দৈনন্দিন কাজের অল-ইন-ওয়ান ইউটিলিটি প্ল্যাটফর্ম"
                            else
                                "Your All-in-One Everyday Android Utility Platform",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                    }
                }

                // Body Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Intro Description
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "মিক্সটুলস (MixTools) হলো একটি শক্তিশালী, আধুনিক এবং দ্রুতগতির মাল্টি-পারপাস ইউটিলিটি অ্যাপ। দৈনন্দিন হিসাব-নিকাশ, টেক্সট প্রসেসিং, ফোন ডায়াগনস্টিকস ও ফাইল জেনারেশনের সব প্রয়োজনীয় সমাধান এক অ্যাপের ভেতরেই সাজানো হয়েছে।"
                        else
                            "MixTools is a fast, modern, and reliable multi-purpose utility platform designed to simplify your everyday digital tasks. Everything from calculators to text processing, phone diagnostics, and file generation—all in one place.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // Features Header
                    Text(
                        text = if (language == AppLanguage.BANGLA) "প্রধান ফিচার ও সেবাসমূহ" else "Core Features & Capabilities",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Feature Cards
                    FeatureDetailItem(
                        icon = Icons.Default.TextFields,
                        iconTint = Color(0xFF2563EB),
                        title = if (language == AppLanguage.BANGLA) "টেক্সট ও রাইটার স্টুডিও" else "Text & Writer Studio",
                        description = if (language == AppLanguage.BANGLA)
                            "কেস কনভার্টার (Upper/Lower/Title), ওয়ার্ড ও ক্যারেক্টার কাউন্টার, বেস৬৪ এনকোডার-ডিকোডার এবং A4/A5 পেজ ফরম্যাটিং সহ সমৃদ্ধ ডকুমেন্ট রাইটার।"
                        else
                            "Case converter, live word & character counter, Base64 encoder/decoder, and rich document writing with A4/A5 page support."
                    )

                    FeatureDetailItem(
                        icon = Icons.Default.Calculate,
                        iconTint = Color(0xFFF97316),
                        title = if (language == AppLanguage.BANGLA) "স্মার্ট ক্যালকুলেটর ও কনভার্টার" else "Smart Calculators & Converters",
                        description = if (language == AppLanguage.BANGLA)
                            "বৈজ্ঞানিক ও সাধারণ ক্যালকুলেটর, বয়স ও দিন গণনা, BMI বডি ম্যাস ইনডেক্স স্বাস্থ্য পরিমাপক এবং তাপমাত্রা, ওজন ও দৈর্ঘ্যের একক রূপান্তর।"
                        else
                            "Standard & scientific calculators, precise age finder (years, months, days), BMI health index, and unit conversions."
                    )

                    FeatureDetailItem(
                        icon = Icons.Default.PhoneAndroid,
                        iconTint = Color(0xFF8B5CF6),
                        title = if (language == AppLanguage.BANGLA) "ডিভাইস ও সিস্টেম ম্যানেজার" else "Device & System Manager",
                        description = if (language == AppLanguage.BANGLA)
                            "লাইভ ফোনের মডেল নাম, হার্ডওয়্যার স্পেক্স, র‍্যাম ও স্টোরেজ পর্যবেক্ষণ, ব্যাটারি লেভেল, তাপমাত্রা ও চার্জিং হেলথ টেলিমেট্রি।"
                        else
                            "Live phone model, hardware specs, RAM & storage telemetry, real-time battery status, temperature, and charging health."
                    )

                    FeatureDetailItem(
                        icon = Icons.Default.PictureAsPdf,
                        iconTint = Color(0xFF06B6D4),
                        title = if (language == AppLanguage.BANGLA) "ইমেজ ও পিডিএফ মেকার" else "Image & PDF Maker",
                        description = if (language == AppLanguage.BANGLA)
                            "যেকোনো টেক্সট থেকে মুহূর্তেই আসল PDF ডকুমেন্ট তৈরি ও শেয়ার, ইমেজ সাইজ মাপার টুল, কালার প্যালেট এক্সট্রাক্টর ও ফটো রোটেশন।"
                        else
                            "Instant high-quality PDF document creation from text, image dimensions analyzer, palette color extractor, and image tools."
                    )

                    FeatureDetailItem(
                        icon = Icons.Default.QrCodeScanner,
                        iconTint = Color(0xFF10B981),
                        title = if (language == AppLanguage.BANGLA) "কিউআর কোড ও নিত্য ইউটিলিটি" else "QR & Everyday Utilities",
                        description = if (language == AppLanguage.BANGLA)
                            "তাত্ক্ষণিক কিউআর কোড জেনারেটর ও দ্রুত ক্যামেরা স্ক্যানার, ফ্ল্যাশলাইট, দিকনির্ণয়কারী কম্পাস এবং প্রিসিশন স্টপওয়াচ।"
                        else
                            "Instant QR code generator & camera scanner, flashlight, digital directional compass, and precision stopwatch."
                    )

                    FeatureDetailItem(
                        icon = Icons.Default.CloudDone,
                        iconTint = Color(0xFF3B82F6),
                        title = if (language == AppLanguage.BANGLA) "ক্লাউড সিঙ্ক ও অটো আপডেট" else "Cloud Sync & Live Updates",
                        description = if (language == AppLanguage.BANGLA)
                            "১-ক্লিকে গুগল অ্যাকাউন্ট সাইন-ইন দিয়ে ফেভারিট ও হিস্ট্রি সংরক্ষণ এবং অ্যাডমিন প্যানেল থেকে তাৎক্ষণিক প্রিমিয়াম আপডেট সুবিধা।"
                        else
                            "Seamless 1-click Google account sync for favorites and history, plus instant in-app update broadcasts from the admin console."
                    )

                    // Highlights Pill Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppAdvantageChip(
                            icon = Icons.Default.Speed,
                            text = if (language == AppLanguage.BANGLA) "দ্রুত ও মসৃণ" else "Ultra Fast",
                            modifier = Modifier.weight(1f)
                        )
                        AppAdvantageChip(
                            icon = Icons.Default.Security,
                            text = if (language == AppLanguage.BANGLA) "১০০% নিরাপদ" else "Secure",
                            modifier = Modifier.weight(1f)
                        )
                        AppAdvantageChip(
                            icon = Icons.Default.CheckCircle,
                            text = if (language == AppLanguage.BANGLA) "অফলাইন ফ্রেন্ডলি" else "Offline Ready",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Developer Card (The App Made By An Naem)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = openDeveloperBlog)
                            .testTag("about_dialog_developer_card"),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "ডেভেলপার পরিচিতি" else "DEVELOPER CREDIT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "The App Made By An Naem",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "https://annaem143.blogspot.com/?m=1",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "Visit Blog",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = openDeveloperBlog,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("visit_blog_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (language == AppLanguage.BANGLA) "ব্লগ ভিজিট" else "Visit Blog")
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("close_about_dialog_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (language == AppLanguage.BANGLA) "ঠিক আছে" else "Close")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureDetailItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun AppAdvantageChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun DeveloperAttributionCard(
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("developer_attribution_card"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "The App Made By An Naem",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier.size(10.dp)
            )
        }
    }
}
