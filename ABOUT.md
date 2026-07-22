# About RoadRoot

RoadRoot is a native Android application designed to simplify long-term project planning.

Instead of managing disconnected task lists, RoadRoot allows projects to be broken down into structured roadmaps composed of milestones, sub-milestones, and actionable tasks. Progress is calculated automatically as work is completed, providing a clear overview of the entire project lifecycle.

Built with Jetpack Compose and Material Design 3, the application emphasizes clarity, performance, and an offline-first experience.

---

# Core Concepts

## Projects

Projects act as the highest level of organization.

Each project maintains its own:

- Name
- Description
- Accent color
- Independent roadmap
- Progress tracking

Multiple projects can coexist without affecting one another.

---

## Roadmaps

Every project contains a hierarchical roadmap.

Roadmap items may contain an unlimited number of child items, allowing projects to grow naturally from high-level goals into detailed implementation plans.

Example:

```
Build Android App
├── Planning
│   ├── Requirements
│   └── Roadmap
├── Design
│   ├── Wireframes
│   ├── Design System
│   └── Assets
├── Development
│   ├── Database
│   │   ├── Entities
│   │   ├── DAOs
│   │   └── Migrations
│   ├── UI
│   │   ├── Home Screen
│   │   ├── Roadmap Screen
│   │   └── Settings
│   └── Testing
│       ├── Unit Tests
│       └── UI Tests
└── Release
    ├── Play Store
    └── Version 1.0
```

Each roadmap item supports:

- Title
- Description
- Completion status
- Due date
- Child milestones

---

# Features

## Multi-Project Management

Create and organize multiple independent projects while maintaining separate roadmaps and progress tracking for each.

---

## Unlimited Hierarchy

Roadmap items are self-referential, allowing unlimited nesting depth without artificial limitations.

---

## Automatic Progress Tracking

Project completion percentages are calculated automatically based on the completion state of roadmap items.

Progress updates instantly as milestones are completed.

---

## Project Descriptions

Each project includes a dedicated description for documenting objectives, requirements, references, or planning notes.

---

## Roadmap Item Descriptions

Every milestone can contain its own detailed description, making it suitable for implementation notes, documentation, or specifications.

---

## Drag & Drop Reordering

Roadmap items can be reordered directly through drag-and-drop interactions.

Changes are persisted automatically.

---

## Visual Tree Preview

Parent milestones display a compact preview of their child hierarchy, improving navigation without requiring every branch to be expanded.

---

## Search

Search across projects and roadmap items from a single interface.

Results update dynamically while typing.

---

## Templates

Quickly create projects using predefined templates including:

- Web Development
- Android App
- Novel
- Portfolio
- Research
- Game Development
- Startup
- Blank Project

Templates provide an initial roadmap structure that can be customized freely.

---

## Due Dates

Assign optional due dates to roadmap items.

Overdue milestones are highlighted automatically.

---

## Export

Projects can be exported as:

- Markdown
- JSON

Exports can be shared directly through Android's native Share Sheet.

---

## Backup & Restore

Create backups of the local database and restore them whenever needed.

Designed for offline reliability without requiring cloud services.

---

## Statistics

View project-wide statistics including:

- Overall completion
- Completed milestones
- Pending milestones
- Overdue items

---

# Design Philosophy

The interface minimizes distractions while exposing powerful planning capabilities through a clean, structured workflow.

The application is built around three principles:

- Projects should remain understandable regardless of size.
- Planning should feel lightweight instead of overwhelming.
- Everything should work without requiring an internet connection.

---

# Future Direction

RoadRoot will continue evolving with improvements to planning, visualization, and project organization while maintaining its offline-first philosophy and native Android experience.