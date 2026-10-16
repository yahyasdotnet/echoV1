import librosa
import numpy as np

# Class for converting soundwaves to spectrograms
# 10 / 10 / 2025

class ConvertingSoundwaves:
    def __init__(self, input_file, output_file):
        self.input_file = input_file
        self.output_file = output_file

    def load_audio(self):
        audio, sample_rate = librosa.load(
            self.input_file,
            sr=None,
            mono=True
        )
        return audio, sample_rate

    def compute_stft(self, audio):
        stft = librosa.stft(audio)
        return stft

    def compute_spectrogram(self, stft):
        spectrogram = librosa.amplitude_to_db(
            np.abs(stft),
            ref=np.max
        )
        return spectrogram

    def show_spectrogram(self, spectrogram):
        import matplotlib.pyplot as plt
        plt.figure(figsize=(10, 4))
        librosa.display.specshow(spectrogram, sr=22050, x_axis='time', y_axis='log')
        plt.colorbar(format='%+2.0f dB')
        plt.title('Spectrogram')
        plt.tight_layout()
        plt.show()

    def save_spectrogram(self, spectrogram):
        np.save(self.output_file, spectrogram)
        print(f"Converted {self.input_file} to {self.output_file}")


    def convert(self):
        audio, sample_rate = librosa.load(
            self.input_file,
            sr=None,
            mono=True
        )

        stft = librosa.stft(audio)

        spectrogram = librosa.amplitude_to_db(
            np.abs(stft),
            ref=np.max
        )

        np.save(self.output_file, spectrogram)

        print(f"Converted {self.input_file} to {self.output_file}")

        return spectrogram