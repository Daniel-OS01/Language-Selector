# Playbook 05 — Coroutines and ViewModel races

## Problem / when you need this

Multiple UI events start the same IO work (`refresh`, `toggle filter`, `import finished` → refresh). Overlapping coroutines:

- Interleave `list.clear()` / `addAll()`
- Leave `isLoading` stuck
- Apply stale results after a newer refresh finished

## Recommended architecture

**Cancel-and-replace** (same pattern as debounced search):

```kotlin
private var fillListJob: Job? = null

fun fillListOfApps() {
    fillListJob?.cancel()
    fillListJob = viewModelScope.launch(Dispatchers.IO) {
        // build immutable result first
        val sortedList = /* ... */
        ensureActive() // cancelled older refresh must not publish stale UI
        // then mutate UI state once
    }
}
```

**Busy operations** (export/import/apply):

```kotlin
try {
    block()
} catch (e: CancellationException) {
    throw e  // never convert cancellation into a user error
} catch (e: Exception) {
    // show message
} finally {
    // clear isBusy
}
```

Optional: `Mutex` if you must queue instead of cancel — prefer cancel for “latest wins” list loads.

## Concrete checklist

- [ ] One `Job?` per overlapping operation family
- [ ] Cancel previous before launching
- [ ] Build results off the shared mutable list; write once at the end
- [ ] Rethrow `CancellationException` in broad `catch (Exception)`
- [ ] Prefer `viewModelScope` so work dies with the screen

## Pitfalls we hit + fixes (Neo)

| Pitfall | Fix |
| --- | --- |
| `init`, `refreshFromSystem`, `toggleSystemAppsVisibility` all called `fillListOfApps()` unconstrained | Introduced `fillListJob` cancel-and-replace |
| `runBusy` caught `CancellationException` as “Operation failed” | Rethrow before generic handler |
| CodeRabbit suggested Mutex | Cancel-and-replace sufficient for latest-wins refresh |

## File map

| Neo | In your app |
| --- | --- |
| `MainScreenVm.fillListJob` / `searchJob` | List + search jobs |
| `SettingsVm.runBusy` | Gated IO with cancellation hygiene |
| `AppListRefreshBus` | Cross-screen “please refresh” signal |

## Validation

Manual: spam filter toggles + import while list loads — UI should settle on the latest request without mixed lists.

Unit-test pure helpers; race logic is best covered by instrumentation or careful manual QA (Neo has no Compose UI tests yet).
