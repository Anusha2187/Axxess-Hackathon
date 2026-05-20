# CareConnect — Home Health Nurse Discovery Platform

## Overview

CareConnect is a healthcare marketplace MVP designed to connect patients and caregivers with nearby licensed home health nurses through a mobile Android application.

The platform enables:
- Nurse registration and profile creation
- Patient registration and nurse discovery
- Zip-code-based nurse matching
- Real-time nurse search using Firebase Firestore
- Location-enabled nurse discovery
- Direct patient-to-nurse communication workflows

The project was designed as an MVP focused on solving accessibility and discovery challenges in home healthcare staffing.

## Problem Statement

Patients and caregivers often struggle to quickly find nearby home health nurses without relying on expensive staffing agencies or fragmented referral systems.

Independent nurses also lack a centralized platform to:
- advertise availability
- manage visibility
- connect directly with patients

CareConnect addresses this gap through a lightweight healthcare discovery marketplace.

## Key Features

### Patient Features
- User registration
- Nurse search by zip code
- Device location integration
- RecyclerView-based nurse listings
- Call and email actions

### Nurse Features
- Nurse registration workflow
- Profile creation and Firestore storage
- Zip-code-based discoverability

### Platform Features
- Firebase Authentication
- Firestore database integration
- Android location services
- Real-time search filtering
- Modular Kotlin architecture


## Architecture

The application follows a modular Android architecture:

- UI Layer → Activities + RecyclerView Adapter
- Repository Layer → Firestore data operations
- Service Layer → Location and geocoding services
- Firebase Backend → Authentication + Firestore

Core Components:
- `HealthRepository.kt`
- `LocationService.kt`
- `NursesAdapter.kt`
- `Models.kt`

## Business Value

CareConnect demonstrates how mobile healthcare marketplaces can:
- reduce patient search friction
- improve healthcare accessibility
- support independent nurses
- streamline local healthcare staffing
- enable scalable healthcare discovery platforms


