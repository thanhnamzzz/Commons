plugins {
	alias(libs.plugins.android.library)
	`maven-publish`
}

android {
	namespace = "common.libs"
	compileSdk {
		version = release(36) {
			minorApiLevel = 1
		}
	}

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
	publishing {
		singleVariant("release")
	}

	buildFeatures { viewBinding = true }
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.sdp.android)
//	Glide
//	implementation(libs.glide)
	implementation(libs.androidx.constraintlayout)
}

publishing.publications {
	create<MavenPublication>("release") {
		afterEvaluate {
			from(components["release"])
		}
	}
}