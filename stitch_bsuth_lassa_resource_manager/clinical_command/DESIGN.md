---
name: Clinical Command
colors:
  surface: '#fcf8fa'
  surface-dim: '#dcd9db'
  surface-bright: '#fcf8fa'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f5'
  surface-container: '#f0edef'
  surface-container-high: '#eae7e9'
  surface-container-highest: '#e4e2e4'
  on-surface: '#1b1b1d'
  on-surface-variant: '#45464d'
  inverse-surface: '#303032'
  inverse-on-surface: '#f3f0f2'
  outline: '#76777d'
  outline-variant: '#c6c6cd'
  surface-tint: '#565e74'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#131b2e'
  on-primary-container: '#7c839b'
  inverse-primary: '#bec6e0'
  secondary: '#515f74'
  on-secondary: '#ffffff'
  secondary-container: '#d5e3fd'
  on-secondary-container: '#57657b'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#271901'
  on-tertiary-container: '#98805d'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2fd'
  primary-fixed-dim: '#bec6e0'
  on-primary-fixed: '#131b2e'
  on-primary-fixed-variant: '#3f465c'
  secondary-fixed: '#d5e3fd'
  secondary-fixed-dim: '#b9c7e0'
  on-secondary-fixed: '#0d1c2f'
  on-secondary-fixed-variant: '#3a485c'
  tertiary-fixed: '#fcdeb5'
  tertiary-fixed-dim: '#dec29a'
  on-tertiary-fixed: '#271901'
  on-tertiary-fixed-variant: '#574425'
  background: '#fcf8fa'
  on-background: '#1b1b1d'
  surface-variant: '#e4e2e4'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 34px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  data-mono:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 32px
---

## Brand & Style
The design system is engineered for high-stakes clinical environments, specifically the Lassa Fever Unit. The brand personality is **authoritative, precise, and stoic**. It prioritizes information density and cognitive clarity over aesthetic flourish, evoking a sense of "quiet confidence" for healthcare administrators and frontline clinicians.

The design style follows a **Modern Corporate/Clinical** approach:
- **Utilitarian Precision:** Every pixel serves a functional purpose. Decorations are stripped back to allow critical data—such as bed availability and patient status—to take precedence.
- **High Information Density:** Controlled whitespace ensures that a large volume of resource data is visible at a single glance without overwhelming the user.
- **Reliability:** A rigid grid and consistent alignment reinforce the system’s stability, critical for emergency medical operations.

## Colors
This design system utilizes a restrained, professional palette optimized for long-duration monitor use in clinical settings.

- **Primary & Secondary:** Deep Navy and Slate are used for navigation, headers, and primary actions to establish an authoritative hierarchy.
- **Surface & Background:** Cool White and pure White provide a sterile, high-contrast canvas that mimics the cleanliness of a medical facility.
- **Functional Status:** Colors are used strictly for status. **Crimson (#EF4444)** is reserved for critical life-safety alerts or danger zones. **Amber (#F59E0B)** indicates transitional states (e.g., bed cleaning). **Emerald (#10B981)** signifies "Ready" or "Safe," and **Sky Blue (#0EA5E9)** is used for administrative information and reservations.

## Typography
The typography system uses **Inter** to ensure maximum legibility at small sizes and across various screen resolutions.

- **Data-First Approach:** For numerical data (patient IDs, bed numbers, timestamps), use tabular figures (`tnum`) to ensure numbers align vertically in lists and tables.
- **Strict Hierarchy:** Labels use an uppercase style with slight letter-spacing to differentiate metadata from body content.
- **Scale:** Headlines are kept modest in size to preserve vertical space for dashboard widgets and resource grids.

## Layout & Spacing
The design system employs a **Fixed-Fluid Hybrid Grid** system.
- **The 4px Rhythm:** All spacing (padding, margins, heights) must be a multiple of 4px.
- **Desktop:** A 12-column grid with 16px gutters. Sidebars are fixed at 280px to maximize the fluid "Command Center" dashboard area.
- **Information Density:** Use `sm` (8px) and `md` (16px) spacing for internal component padding to maintain a compact, clinical feel.
- **Mobile:** Elements reflow into a single column with 16px margins; navigation transitions to a bottom bar or condensed drawer.

## Elevation & Depth
Depth is signaled through **Tonal Layering** and **Low-Contrast Outlines** rather than heavy shadows.

- **Surface Tiers:** The background is #F8FAFC. Active work surfaces (cards, tables) are pure #FFFFFF with a 1px border of #E2E8F0.
- **Subtle Shadows:** When elevation is required (e.g., modals or dropdowns), use a single, ultra-soft shadow: `0px 4px 6px -1px rgba(15, 23, 42, 0.08)`.
- **Active State:** Focus states for inputs and buttons use a 2px outer ring in #0EA5E9 with a 1px white offset.

## Shapes
Shapes are **structured and conservative**.
- **Corner Radius:** A universal 4px to 6px radius is applied to buttons, cards, and input fields. This provides a modern look while remaining "serious" and clinical.
- **Status Indicators:** Use small circular dots or narrow vertical bars on the left edge of cards to indicate status, avoiding large blocks of color that could distract from text.

## Components
- **Buttons:** Primary buttons are Solid Deep Navy (#0F172A). Secondary buttons use a 1px border (#CBD5E1) with Slate text. No gradients. High-contrast white text for primary actions.
- **Status Chips:** Small, 4px rounded tags with subtle background tints and high-contrast text (e.g., Success: #DCFCE7 background, #10B981 text).
- **Data Tables:** The core of the system. 1px horizontal dividers only (#F1F5F9). Row hover state uses #F8FAFC. Header cells use `label-md` typography.
- **Input Fields:** 1px border (#CBD5E1), 4px radius. On focus, the border changes to #0EA5E9.
- **Resource Cards:** Used for bed or equipment tracking. Must include a clear "Status Bar" on the top or left edge.
- **Alert Stacks:** Critical alerts (Crimson) appear at the top of the viewport with a subtle "pulse" animation on the border only.
- **KPI Widgets:** Large numerical values using `display-lg` with a secondary `label-md` descriptor below.