plugins {
	alias(libs.plugins.android.application)
}

android {
	namespace = "common.commons"
	compileSdk {
		version = release(36) {
			minorApiLevel = 1
		}
	}

	defaultConfig {
		applicationId = "common.commons"
		minSdk = 23
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

	buildFeatures {
		viewBinding = true
	}
}

dependencies {

	implementation(libs.androidx.activity.ktx)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.constraintlayout)
	implementation(project(":libs"))
}