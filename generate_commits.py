
import random
import subprocess
from datetime import datetime, timedelta, timezone
from pathlib import Path

REPO = Path.cwd()
START = datetime(2026, 9, 1, tzinfo=timezone.utc)
END = datetime.now(timezone.utc)

# Number of commits to generate
NUM_COMMITS = 40

def git(*args, env=None):
    subprocess.run(
        ["git", *args],
        cwd=REPO,
        env=env,
        check=True,
        stdout=subprocess.DEVNULL,
    )

# Ensure this is a Git repository
subprocess.run(["git", "rev-parse", "--git-dir"],
               cwd=REPO, check=True, stdout=subprocess.DEVNULL)

# Configure a local demo identity if needed
git("config", "user.name", "Demo User")
git("config", "user.email", "demo@example.com")

random.seed()

span = int((END - START).total_seconds())
dates = sorted(
    START + timedelta(seconds=random.randint(0, span))
    for _ in range(NUM_COMMITS)
)

for i, dt in enumerate(dates, start=1):
    filename = REPO / "demo_log.txt"
    with filename.open("a", encoding="utf-8") as f:
        f.write(f"Demo entry {i}: {dt.isoformat()}\n")

    # Use the same timestamp for author and committer.
    stamp = dt.strftime("%Y-%m-%dT%H:%M:%S+00:00")
    import os
    env = os.environ.copy()
    env["GIT_AUTHOR_DATE"] = stamp
    env["GIT_COMMITTER_DATE"] = stamp

    git("add", "demo_log.txt", env=env)
    git("commit", "-m", f"Add demo entry {i}", env=env)

print(f"Created {NUM_COMMITS} demo commits.")
print("Inspect them with: git log --date=iso --pretty=fuller")
