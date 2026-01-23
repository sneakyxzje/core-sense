export function useDebounce<T>(valueGetter: () => T, delay: number = 500) {
  let debounced = $state($state.snapshot(valueGetter()));
  $effect(() => {
    const value = valueGetter();
    JSON.stringify(valueGetter());
    const handler = setTimeout(() => {
      debounced = $state.snapshot(value);
    }, delay);

    return () => clearTimeout(handler);
  });

  return {
    get current() {
      return debounced;
    },
  };
}
