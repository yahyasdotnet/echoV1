Features so far: 

Instrument recognition. Use the NSynth or IRMAS datasets to classify instruments. A CNN on mel-spectrograms works well, and you can try transfer learning from a pretrained audio model like VGGish or PANNs.

Automatic chord recognition. Predict chord labels from chromagrams using an HMM, a CRNN, or a transformer. Datasets include the Isophonics and McGill Billboard sets.

Music tagging with pretrained models. Fine-tune MERT, CLAP, or wav2vec2 on the MagnaTagATune or Jamendo datasets for multi-label tagging.

Source separation. Use Demucs or Spleeter to split vocals, drums, bass, and other. Then build something on top, like a karaoke maker or a drum-stem analyzer.

Cover song or duplicate detection. Use chroma features with dynamic time warping, or learn embeddings with a Siamese network on the Da-TACOS dataset.