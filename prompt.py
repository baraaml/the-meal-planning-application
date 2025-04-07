#!/usr/local/pyenv/shims/python3
import os
import sys
import argparse

def print_tree(directory, prefix='', output_file=None):
    """
    Recursively print the directory tree and file contents, excluding certain folders and file types.

    Excludes:
    - Folders: .git, node_modules, assets, venv
    - Files: prompt.py, index.html, meals.csv
    - Extensions: .svg, .js, .csv
    """
    try:
        entries = [
            entry for entry in os.listdir(directory)
            if entry not in ('node_modules', 'assets', 'venv', '.git', 'prompt.py', 'index.html', 'meals.csv')
            and not entry.endswith(('.svg', '.js', '.csv'))
        ]
        entries.sort()  # Sort entries alphabetically

        for i, entry in enumerate(entries):
            entry_path = os.path.join(directory, entry)
            is_last = (i == len(entries) - 1)

            connector = "└── " if is_last else "├── "

            if output_file:
                output_file.write(f"{prefix}{connector}{entry}\n")
            else:
                print(f"{prefix}{connector}{entry}")

            if os.path.isdir(entry_path):
                new_prefix = prefix + ("    " if is_last else "│   ")
                print_tree(entry_path, new_prefix, output_file)
            else:
                # Skip reading files with .csv extension
                if entry.endswith('.csv'):
                    continue
                try:
                    with open(entry_path, 'r', encoding='utf-8') as file:
                        content = file.read()
                        separator = "//" * 40
                        if output_file:
                            output_file.write(f"\n{separator}\n")
                            output_file.write(content)
                            output_file.write(f"\n{separator}\n\n")
                        else:
                            print(f"\n{separator}")
                            print(content)
                            print(f"{separator}\n")
                except Exception as e:
                    error_msg = f"Could not read file '{entry_path}': {e}"
                    if output_file:
                        output_file.write(f"{error_msg}\n")
                    else:
                        print(error_msg)
    except Exception as e:
        error_msg = f"An error occurred while accessing '{directory}': {e}"
        if output_file:
            output_file.write(f"{error_msg}\n")
        else:
            print(error_msg)

def main():
    parser = argparse.ArgumentParser(description='Print directory tree while excluding certain folders and file types')
    parser.add_argument('input_dir', help='Input directory path')

    args = parser.parse_args()

    if not os.path.isdir(args.input_dir):
        print("Error: The specified input path is not a valid directory.")
        sys.exit(1)

    output_path = "chat.txt"
    try:
        with open(output_path, 'w', encoding='utf-8') as output_file:
            output_file.write(f"Directory Tree: {args.input_dir}\n")
            output_file.write("=" * 80 + "\n\n")
            print_tree(args.input_dir, output_file=output_file)
        print(f"Output written to {output_path}")
    except Exception as e:
        print(f"Error writing to output file: {e}")

if __name__ == "__main__":
    main()
