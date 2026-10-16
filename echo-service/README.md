Music Recommendation System Using Sound Wave Visualization

Most music recommendation systems on popular streaming platforms like Apple Music and YouTube rely heavily on collaborative filtering, using users' listening habits, playlists, and preferences to recommend new songs.

But what if you want recommendations based on the music itself rather than what other people listen to?

This project explores an alternative approach to music recommendation by analyzing the visual representation of audio. By transforming songs into spectrograms or other sound visualizations, the system aims to identify similarities between tracks based on their audio characteristics.

Using YOLOv8 and computer vision techniques, the goal is to develop a system that takes an uploaded song and finds other tracks with similar visual audio patterns, offering a more audio-focused approach to music discovery.

System Architecture

The following diagram illustrates the proposed workflow, from uploading a song to generating personalized, audio-based recommendations.

flowchart TD
    A["Music Catalog<br/>Songs, Albums, Artists"] --> B["Audio Processing Pipeline"]
    B --> C["Generate Spectrograms"]
    C --> D["Extract Audio Features"]
    D --> E[("Feature Database<br/>Metadata + Feature Vectors")]
    E --> F["Similarity Search"]
    G["User Uploads a Song"] --> H["Process Uploaded Audio"]
    H --> I["Extract Query Features"]
    I --> F
    F --> J["Rank Similar Tracks"]
    J --> K["Recommended Songs<br/>Artist + Album + Track"]

    style A fill:#1e293b,color:#ffffff,stroke:#64748b
    style E fill:#164e63,color:#ffffff,stroke:#22d3ee
    style F fill:#312e81,color:#ffffff,stroke:#818cf8
    style K fill:#14532d,color:#ffffff,stroke:#4ade80

Proposed Workflow
Music Catalog: Build a collection of songs with their associated artist, album, and track metadata.
Audio Processing: Convert audio files into spectrograms that represent frequency content over time.
Feature Extraction: Use computer vision techniques, including YOLOv8 experiments, to extract useful representations from spectrograms.
Feature Database: Store track metadata and extracted numerical features for efficient retrieval.
Similarity Search: Compare the uploaded song's features against the existing music catalog.
Recommendations: Rank and return the most similar tracks based on their audio characteristics.
Project Goal

The goal is to explore whether audio-based similarity can provide an alternative approach to music discovery, focusing on the acoustic characteristics of songs rather than relying primarily on listening history or popularity.