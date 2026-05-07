# Compose UI Patterns Quick Reference

Quick reference for Quant Vault Android Compose UI patterns during code reviews. For comprehensive details, read `docs/ARCHITECTURE.md` and `docs/STYLE_AND_BEST_PRACTICES.md`.

## Component Reuse

**✅ GOOD - Uses existing components**:
```kotlin
Quant VaultButton(
    text = "Submit",
    onClick = onSubmit
)

Quant VaultTextField(
    value = text,
    onValueChange = onTextChange,
    label = "Email"
)
```

**❌ BAD - Duplicates existing components**:
```kotlin
// ❌ Recreating Quant VaultButton
Button(
    onClick = onSubmit,
    colors = ButtonDefaults.buttonColors(
        containerColor = Quant VaultTheme.colorScheme.primary
    )
) {
    Text("Submit")
}
```

**Key Rules**:
- Check `:ui` module for existing components before creating custom ones
- Use Quant VaultButton, Quant VaultTextField, etc. for consistency
- Place new reusable components in `:ui` module

---

## Theme Usage

**✅ GOOD - Uses theme**:
```kotlin
Text(
    text = "Title",
    style = Quant VaultTheme.typography.titleLarge,
    color = Quant VaultTheme.colorScheme.primary
)

Spacer(modifier = Modifier.height(16.dp))  // Standard spacing
```

**❌ BAD - Hardcoded values**:
```kotlin
Text(
    text = "Title",
    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),  // Use theme
    color = Color(0xFF0066FF)  // Use theme color
)

Spacer(modifier = Modifier.height(17.dp))  // Non-standard spacing
```

**Key Rules**:
- Use `Quant VaultTheme.colorScheme` for colors
- Use `Quant VaultTheme.typography` for text styles
- Use standard spacing (4.dp, 8.dp, 16.dp, 24.dp)

---

## Quick Checklist

### UI Patterns
- [ ] Using existing Quant Vault components from `:ui` module?
- [ ] Using Quant VaultTheme for colors and typography?
- [ ] Using standard spacing values (4, 8, 16, 24 dp)?
- [ ] No hardcoded colors or text styles?
- [ ] UI is stateless (observes state, doesn't modify)?

---

For comprehensive details, always refer to:
- `docs/ARCHITECTURE.md` - Full architecture patterns
- `docs/STYLE_AND_BEST_PRACTICES.md` - Complete style guide
