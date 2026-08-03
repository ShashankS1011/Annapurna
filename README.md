# Project Overview: Annapurna

**Annapurna** — named after the Hindu Goddess of Food and Nourishment (*Anna* meaning food, and *Purna* meaning complete or perfect) — is an enterprise-grade, native Android application engineered to function as a permanent digital repository and active cooking assistant. The application honors the warmth, heritage, and emotional connection of home-cooked meals by providing a sacred, secure space to safeguard treasured family recipes.

By bridging traditional home culinary heritage with modern software engineering, Annapurna ensures that sacred family formulas, cherished home-cooked meals, step-by-step preparation notes, and high-definition video walkthroughs remain fully accessible, privacy-focused, and permanently stored on the local device.

---

## The Philosophy: Preserving Home-Cooked Heritage

Home-cooked food carries traditions, health benefits, and emotional bonds that processed or commercial meals simply cannot replicate. However, authentic family recipes are often lost across generations due to unwritten instructions, lost hand-written notebooks, or reliance on memory alone. 

Annapurna digitizes and elevates home cooking by:
* **Preserving Generational Memory:** Protecting grandmother’s and mother’s unwritten culinary secrets in a permanent digital vault.
* **Celebrating Home Chefs:** Giving proper chef attribution and recognition to family members who sustain the household through wholesome meals.
* **Encouraging Healthy Home Meals:** Making it seamless to re-create comforting home-cooked food by keeping instructions, video techniques, and secret tips organized in one distraction-free place.

---

## Architectural & Technical Specifications

### Core Framework & Architecture
* **Language & SDK:** Java (Android SDK, targeted at modern API levels).
* **Architectural Pattern:** Model-View-ViewModel (MVVM) coupled with `LiveData` and `Observer` patterns to enforce strict separation of concerns, reactive UI updates, and robust state lifecycle management.
* **Component Lifecycle Handling:** Custom `ViewModel` implementations ensure UI controllers (`Activities` and `Fragments`) remain stateless across configuration changes (e.g., screen rotations, multi-window mode entry).

### Data Layer & Storage Engine
* **Database Engine:** Room Persistence Library (SQLite abstraction layer).
* **Data Access Objects (DAOs):** Custom DAO implementations utilizing compile-time SQL validation, asynchronous query execution via background worker threads, and dynamic `LiveData` stream wrappers.
* **Database Migrations:** Configured with structured migration strategies to support schema evolution over time without data corruption or loss.

### Media Processing & Playback Pipeline
* **Media Engine:** Jetpack Media3 (`androidx.media3.exoplayer`) integration.
* **Hardware Acceleration:** Native utilization of system-level video decoders for low-latency, energy-efficient rendering during long cooking sessions.
* **Playback Management:** Custom player initialization, lifecycle synchronization (binding playback state to activity `onStart`/`onStop`), and automatic memory surface cleanup to prevent memory leaks.

### Security, System Permissions & Scoped Storage
* **Storage Access Framework (SAF):** Full compliance with Android Scoped Storage mandates using `Intent.ACTION_OPEN_DOCUMENT`.
* **Persistable URI Permissions:** Utilizes `ContentResolver.takePersistableUriPermission()` to maintain persistent local read access to media files stored in external directories across device reboots without demanding full file-system access.
* **Power Management Subsystem:** Conditional `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` requests bound to screen lifecycle events to prevent OS display sleep timeouts during recipe execution.

### UI/UX Design System
* **Design Standards:** Material Design 3 (MD3) implementation incorporating dynamic color theming, adaptive dark/light palettes, card overlays, and elevated surfaces.
* **Form & Input Management:** Structured form layouts utilizing `TextInputLayout` and `TextInputEditText` for real-time validation and clean focus transitions.
* **Responsive Layouts:** Implements `ConstraintLayout` and `CoordinatorLayout` trees alongside `RecyclerView` for optimized layout flattening, scrolling performance, and view-recycling efficiency.

---

## Key Functional Components

### 1. Recipe Vault & Metadata Management
* **Comprehensive Schema:** Captures extensive metadata per recipe entity, including recipe name, chef attribution (e.g., *Grandma*, *Mom*, *Chef Ranveer*), primary meal category (*Breakfast*, *Main Course*, *Dessert*, *Beverage*, *Snacks*, *Other*), special occasion tagging (*Diwali*, *Sunday Special*), and granular step-by-step cooking notes.
* **Mom’s Secret Tip Highlight Cards:** Dedicated visual cards engineered to draw explicit attention to family culinary secrets, non-standard ingredient tweaks, or proprietary prep methods passed down through generations.
* **Data Integrity:** Strict entity field validation preventing empty submissions, null-pointer exceptions, or database corruption during write operations.

### 2. Video Player Integration & State Preservation
* **Embedded Viewport:** Integrates ExoPlayer directly inside the details UI via `PlayerView`, providing dedicated controls for play/pause, scrub, and time tracking.
* **Exact Timestamp Recovery:** Intercepts system termination and pause signals to save the current playback timestamp (`videoPlaybackPosition`) to the Room database.
* **Resume Capability:** Upon opening a recipe card, the player automatically pre-buffers the video file and seeks directly to the exact millisecond where the user last left off, enabling efficient step-by-step visual learning.

### 3. Active Kitchen Assistance Mode
* **Hands-Free Screen Management:** Features a dedicated "Cooking Mode" engine that forces the mobile device display to remain active while viewing recipe steps. This eliminates screen timeouts, preventing cooks from needing to unlock their phones with wet, flour-covered, or oily hands.
* **Dynamic Time-Aware Context:** The primary dashboard evaluates system hardware time to calculate dynamic time-of-day buckets (Morning, Afternoon, Evening, Night) and generates custom contextual greetings for the user.

### 4. Search, Filtering & Bookmark Capabilities
* **Real-Time Dynamic Querying:** Utilizes in-memory and database-level live filtering using text-change observers (`TextWatcher`) to filter large recipe collections instantaneously by recipe title, chef name, or category keywords.
* **Bookmark & Favorites Subsystem:** Offers a quick-access boolean toggle system (`isFavorite`) to separate everyday quick home recipes from special event or bookmarked dishes.
* **Intelligent Empty States:** Replaces empty screens with responsive feedback views, dynamically instructing the user on how to populate their vault or adjust search parameters when no matching entities exist.

### 5. Sharing & Export Subsystem
* **Structured Text Payload Generator:** Converts database entities into clean, formatted Markdown/plain-text blocks containing recipe titles, chef credits, ingredient lists, secret tips, and step-by-step preparation notes.
* **System Chooser Integration:** Dispatches implicit `Intent.ACTION_SEND` requests to share structured recipes natively across WhatsApp, Telegram, SMS, Email, or cloud note-taking tools.

---

## Database Schema Design

### Entity: `recipes`

| Column Name | Data Type | Constraints / Attributes | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | Primary Key, `autoGenerate = true` | Unique autoincrementing record identifier. |
| `name` | `TEXT` | `NOT NULL` | The title/name of the dish. |
| `chef` | `TEXT` | Optional | Chef attribution or family member name (e.g., Mom, Grandma). |
| `category` | `TEXT` | Default: `"Other"` | Category classification (Breakfast, Main, etc.). |
| `secretTip` | `TEXT` | Optional | Dedicated highlight field for special family tips or prep secrets. |
| `occasion` | `TEXT` | Optional | Tag for holidays, festivals, or family traditions. |
| `cookingNotes` | `TEXT` | Optional | Comprehensive step-by-step instructions. |
| `videoPath` | `TEXT` | Optional | Persisted URI path string pointing to local video file. |
| `videoPlaybackPosition` | `INTEGER` | Default: `0` | Saved timestamp in milliseconds for media resumption. |
| `isFavorite` | `INTEGER (Boolean)` | Default: `0` | Boolean flag denoting bookmark status. |

---

## Security, System Permissions & Resource Usage

* **Storage Permissions:** 
  * `READ_MEDIA_VIDEO` / `READ_EXTERNAL_STORAGE`: Requested conditionally based on API level to allow user-driven video selection via SAF picker workflows.
* **Power Management:**
  * `WAKE_LOCK`: Utilized conditionally during active recipe viewing to maintain screen state without draining battery unnecessarily when navigating outside recipe detail screens.

---

## License

This project is open-source software licensed under the **MIT License**. You are free to modify, distribute, and build upon this software for personal or commercial projects. See the `LICENSE` file for full terms and details.
