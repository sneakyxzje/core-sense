# CoreSense

> **AI-Powered Form Intelligence for Recruitment Workflows**

CoreSense is an AI-driven system designed to pre-screen, analyze, and structure recruitment form submissions.  
It helps recruiters reduce screening time by generating structured insights before manual review, while keeping humans fully in control of final decisions.

---

## Overview

Recruitment teams often spend excessive time reviewing unqualified or low-signal candidates.  
CoreSense introduces an AI-assisted pre-evaluation layer that analyzes candidate responses and resumes, transforming unstructured data into actionable insights.

The system follows a **human-in-the-loop** approach, ensuring AI recommendations remain transparent, editable, and fully controllable by recruiters.

---

## Recruitment Use Cases

- Automated analysis of candidate form responses and resumes (CVs)
- AI-generated candidate scoring for early-stage filtering
- Extraction of strengths and weaknesses from unstructured text
- Concise candidate summaries before manual review
- Side-by-side candidate comparison
- Automated recruitment pipeline with Kanban-style workflow

---

## Core Features

- **AI-Based Scoring Engine**  
  Generates preliminary candidate scores to help recruiters prioritize candidates efficiently.

- **Strength & Weakness Extraction**  
  Identifies positive and negative signals directly from candidate inputs.

- **Semantic Summarization**  
  Condenses long-form answers into 2–3 concise, high-signal sentences.

- **Candidate Comparison**  
  Enables structured, side-by-side evaluation of multiple candidates.

- **Recruitment Dashboard**  
  Centralized interface for managing campaigns, submissions, and pipeline stages.

- **Workflow Automation**  
  Configurable automation for candidate progression and status updates.

- **Human-in-the-Loop Control**  
  Recruiters can override AI scores, feedback, and decisions at any stage.

---

## System Architecture

- **Frontend:** SvelteKit
- **Backend:** Spring Boot
- **Database:** PostgreSQL
- **AI Engine:** Gemini

---

## Getting Started

### Prerequisites

- Node.js `v18+`
- Java `17+`
- PostgreSQL
- Gemini API Key

### Installation

```bash
# 1. Clone the repository
git clone git@github.com:sneakyxzje/core-sense.git

# 2. Install frontend dependencies
cd core-sense/frontend
npm install

# 3. Environment configuration
# Create a .env file and add your API_KEY

# 4. Run the application

# Frontend
npm run dev

# Backend
./mvn.cmd spring-boot:run
```
