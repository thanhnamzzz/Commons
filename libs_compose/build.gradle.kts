import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	`maven-publish`
}

android {
	namespace = "common.libs.compose"
	compileSdk = 35

	defaultConfig {
		minSdk = 23

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		consumerProguardFiles("consumer-rules.pro")
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlin {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_17)
		}
	}
	publishing {
		singleVariant("release")
	}

	buildFeatures { compose = true }
}

dependencies {
	debugImplementation(libs.androidx.compose.ui.tooling)
	debugImplementation(libs.androidx.compose.ui.test.manifest)

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.runtime)
	implementation(libs.androidx.compose.ui)
//	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.material3)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.compose.foundation)
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
}

publishing.publications {
	create<MavenPublication>("release") {
		afterEvaluate {
			from(components["release"])
		}
		artifactId = "compose"
	}
}