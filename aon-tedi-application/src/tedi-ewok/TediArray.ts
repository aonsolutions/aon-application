export function flat<T>(arr: T[][]): T[] {
  return arr.reduce((acc: T[], curr: T[]) => {
    curr.forEach((nif: T) => acc.push(nif));
    return acc;
  }, []);
}
